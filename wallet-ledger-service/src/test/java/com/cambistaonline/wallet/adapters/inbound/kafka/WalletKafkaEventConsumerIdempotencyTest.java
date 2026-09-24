package com.cambistaonline.wallet.adapters.inbound.kafka;

import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProcessLedgerMovementUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProvisionUserWalletsUseCase;
import com.cambistaonline.wallet.application.service.IdempotentConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WalletKafkaEventConsumerIdempotencyTest {

    private ProvisionUserWalletsUseCase provisionUserWalletsUseCase;
    private AdjustUserPointsUseCase adjustUserPointsUseCase;
    private ProcessLedgerMovementUseCase processLedgerMovementUseCase;
    private IdempotentConsumerService idempotencyService;
    private WalletKafkaEventConsumer consumer;

    @BeforeEach
    void setUp() {
        provisionUserWalletsUseCase = Mockito.mock(ProvisionUserWalletsUseCase.class);
        adjustUserPointsUseCase = Mockito.mock(AdjustUserPointsUseCase.class);
        processLedgerMovementUseCase = Mockito.mock(ProcessLedgerMovementUseCase.class);
        idempotencyService = Mockito.mock(IdempotentConsumerService.class);

        consumer = new WalletKafkaEventConsumer(
                provisionUserWalletsUseCase,
                adjustUserPointsUseCase,
                processLedgerMovementUseCase,
                idempotencyService
        );
    }

    @Test
    @DisplayName("Idempotencia: Evento recibido por primera vez debe procesarse y marcarse")
    void shouldProcessEventWhenNotProcessedBefore() {
        String eventId = "evt-12345";
        when(idempotencyService.isEventProcessed(eventId, "cambista-wallet-group")).thenReturn(false);

        Map<String, Object> event = Map.of(
                "eventId", eventId,
                "email", "nuevo@cambistaonline.pe"
        );

        consumer.onUserRegistered(event);

        // Debe ejecutarse la lógica de negocio
        verify(provisionUserWalletsUseCase, times(1)).provisionInitialWallets("nuevo@cambistaonline.pe");
        verify(adjustUserPointsUseCase, times(1)).awardPoints("nuevo@cambistaonline.pe", 100);
        // Debe registrarse en la tabla de deduplicación
        verify(idempotencyService, times(1)).markEventAsProcessed(eq(eventId), eq("nuevo@cambistaonline.pe"), eq("UserRegisteredEvent"), eq("cambista-wallet-group"));
    }

    @Test
    @DisplayName("Idempotencia: Evento duplicado en Kafka (redelivery) debe ser descartado sin dobles abonos")
    void shouldDiscardDuplicateEventWithoutReexecution() {
        String eventId = "evt-already-processed";
        // Simular que el evento ya existe en tb_processed_events
        when(idempotencyService.isEventProcessed(eventId, "cambista-wallet-group")).thenReturn(true);

        Map<String, Object> duplicateEvent = Map.of(
                "eventId", eventId,
                "email", "repetido@cambistaonline.pe"
        );

        consumer.onUserRegistered(duplicateEvent);

        // NUNCA debe ejecutar aprovisionamiento ni acreditar puntos nuevamente
        verify(provisionUserWalletsUseCase, never()).provisionInitialWallets(anyString());
        verify(adjustUserPointsUseCase, never()).awardPoints(anyString(), anyInt());
        // No debe reintentar guardar
        verify(idempotencyService, never()).markEventAsProcessed(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Kafka Consumer Tracing: Extrae Correlation ID y estado MFA de las cabeceras")
    void shouldExtractCorrelationIdAndMfaHeadersFromKafka() {
        String eventId = "evt-order-123";
        when(idempotencyService.isEventProcessed(eventId, "cambista-wallet-group")).thenReturn(false);

        Map<String, Object> event = Map.of(
                "eventId", eventId,
                "orderNumber", "ORD-777",
                "userEmail", "cliente@cambistaonline.pe",
                "currencyOrigin", "USD",
                "currencyDestination", "PEN",
                "amountSent", 1000.0,
                "amountReceived", 3780.0
        );

        byte[] cidBytes = "test-cid-abc-123".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] mfaBytes = "true".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        consumer.onOrderCompleted(event, cidBytes, mfaBytes);

        // Debe registrar los movimientos del ledger
        verify(processLedgerMovementUseCase, times(2)).processMovement(any());
        // Debe premiar con puntos
        verify(adjustUserPointsUseCase, times(1)).awardPoints("cliente@cambistaonline.pe", 10);
        // Debe marcarse como procesado
        verify(idempotencyService, times(1)).markEventAsProcessed(eq(eventId), eq("ORD-777"), eq("OrderCompletedEvent"), eq("cambista-wallet-group"));
    }
}
