package com.cambistaonline.wallet.adapters.inbound.kafka;

import com.cambistaonline.wallet.application.dto.LedgerMovementCommand;
import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProcessLedgerMovementUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProvisionUserWalletsUseCase;
import com.cambistaonline.wallet.application.service.IdempotentConsumerService;
import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.EntryDirection;
import com.cambistaonline.wallet.domain.model.LedgerMovementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class WalletKafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletKafkaEventConsumer.class);
    private static final String CONSUMER_GROUP = "cambista-wallet-group";

    private final ProvisionUserWalletsUseCase provisionUserWalletsUseCase;
    private final AdjustUserPointsUseCase adjustUserPointsUseCase;
    private final ProcessLedgerMovementUseCase processLedgerMovementUseCase;
    private final IdempotentConsumerService idempotencyService;

    public WalletKafkaEventConsumer(ProvisionUserWalletsUseCase provisionUserWalletsUseCase,
                                   AdjustUserPointsUseCase adjustUserPointsUseCase,
                                   ProcessLedgerMovementUseCase processLedgerMovementUseCase,
                                   IdempotentConsumerService idempotencyService) {
        this.provisionUserWalletsUseCase = provisionUserWalletsUseCase;
        this.adjustUserPointsUseCase = adjustUserPointsUseCase;
        this.processLedgerMovementUseCase = processLedgerMovementUseCase;
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = "cambista.users.registered", groupId = CONSUMER_GROUP)
    @Transactional
    public void onUserRegistered(Map<String, Object> event) {
        String email = (String) event.get("email");
        String eventId = (String) event.getOrDefault("eventId", (String) event.get("userId"));
        if (eventId == null) {
            eventId = "user-reg-" + email;
        }

        // Deduplicación estricta para garantizar idempotencia
        if (idempotencyService.isEventProcessed(eventId, CONSUMER_GROUP)) {
            log.warn("[IDEMPOTENT CONSUMER] ⚠️ Evento duplicado ignorado: UserRegisteredEvent id={}", eventId);
            return;
        }

        log.info("[WALLET KAFKA CONSUMER] 👤 Aprovisionando billeteras multimoneda para nuevo usuario: {}", email);
        provisionUserWalletsUseCase.provisionInitialWallets(email);

        // Bonificación de bienvenida: 100 puntos
        adjustUserPointsUseCase.awardPoints(email, 100);
        log.info("[WALLET KAFKA CONSUMER] 🎁 100 CambiPuntos acreditados por bienvenida a: {}", email);

        // Marcar como procesado atómicamente
        idempotencyService.markEventAsProcessed(eventId, email, "UserRegisteredEvent", CONSUMER_GROUP);
    }

    @KafkaListener(topics = "cambista.orders.completed", groupId = CONSUMER_GROUP)
    @Transactional
    public void onOrderCompleted(
            @org.springframework.messaging.handler.annotation.Payload Map<String, Object> event,
            @org.springframework.messaging.handler.annotation.Header(value = com.cambistaonline.common.security.SecurityContextMetadata.KAFKA_HEADER_CORRELATION_ID, required = false) byte[] correlationIdBytes,
            @org.springframework.messaging.handler.annotation.Header(value = com.cambistaonline.common.security.SecurityContextMetadata.KAFKA_HEADER_MFA_VERIFIED, required = false) byte[] mfaVerifiedBytes) {

        String cid = correlationIdBytes != null ? new String(correlationIdBytes, java.nio.charset.StandardCharsets.UTF_8) : null;
        boolean mfaVerified = mfaVerifiedBytes != null && Boolean.parseBoolean(new String(mfaVerifiedBytes, java.nio.charset.StandardCharsets.UTF_8));
        String email = (String) event.get("userEmail");

        com.cambistaonline.common.tracing.CorrelationContext.setContext(cid, email, mfaVerified);

        try {
            String orderNumber = (String) event.get("orderNumber");
            String eventId = (String) event.getOrDefault("eventId", "order-comp-" + orderNumber);

            // Deduplicación estricta: Evitar doble acreditación o doble débito en el libro mayor
            if (idempotencyService.isEventProcessed(eventId, CONSUMER_GROUP)) {
                log.warn("[IDEMPOTENT CONSUMER] ⚠️ Evento duplicado ignorado: OrderCompletedEvent id={} para orden={}", eventId, orderNumber);
                return;
            }

            String currencyOrigin = (String) event.get("currencyOrigin");
            String currencyDestination = (String) event.get("currencyDestination");
            Object amountSentObj = event.get("amountSent");
            Object amountReceivedObj = event.get("amountReceived");

        if (email == null || orderNumber == null) return;

        BigDecimal amountSent = amountSentObj != null ? new BigDecimal(amountSentObj.toString()) : BigDecimal.ZERO;
        BigDecimal amountReceived = amountReceivedObj != null ? new BigDecimal(amountReceivedObj.toString()) : BigDecimal.ZERO;

        log.info("[WALLET KAFKA CONSUMER] 💳 Liquidando orden {} en Ledger para {}", orderNumber, email);

        // 1. Débito de la moneda de origen entregada por el cliente
        try {
            AccountCurrency originCurr = AccountCurrency.valueOf(currencyOrigin);
            processLedgerMovementUseCase.processMovement(new LedgerMovementCommand(
                    email,
                    originCurr,
                    EntryDirection.DEBIT,
                    amountSent,
                    LedgerMovementType.EXCHANGE_DEBIT,
                    orderNumber,
                    "Liquidación por cambio de divisas entregado"
            ));
        } catch (Exception e) {
            log.warn("[WALLET LEDGER] No se pudo debitar saldo de origen: {}", e.getMessage());
        }

        // 2. Crédito de la moneda destino recibida por el cliente
        try {
            AccountCurrency destCurr = AccountCurrency.valueOf(currencyDestination);
            processLedgerMovementUseCase.processMovement(new LedgerMovementCommand(
                    email,
                    destCurr,
                    EntryDirection.CREDIT,
                    amountReceived,
                    LedgerMovementType.EXCHANGE_CREDIT,
                    orderNumber,
                    "Liquidación por cambio de divisas recibido"
            ));
        } catch (Exception e) {
            log.warn("[WALLET LEDGER] No se pudo acreditar saldo destino: {}", e.getMessage());
        }

        // 3. Recompensa de 10 CambiPuntos por operación completada
        adjustUserPointsUseCase.awardPoints(email, 10);
        log.info("[WALLET KAFKA CONSUMER] ⭐ +10 CambiPuntos otorgados a {} por orden {}", email, orderNumber);

        // Marcar como procesado atómicamente
        idempotencyService.markEventAsProcessed(eventId, orderNumber, "OrderCompletedEvent", CONSUMER_GROUP);
        } finally {
            com.cambistaonline.common.tracing.CorrelationContext.clearContext();
        }
    }
}
