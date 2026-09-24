package com.cambistaonline.order.application.service;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;
import com.cambistaonline.common.grpc.wallet.*;
import com.cambistaonline.common.saga.OrderCompensatedEvent;
import com.cambistaonline.common.saga.SagaStatus;
import com.cambistaonline.order.application.dto.CreateOrderRequest;
import com.cambistaonline.order.application.dto.CreateOrderResponse;
import com.cambistaonline.order.application.ports.outbound.ExchangeRateClientPort;
import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.application.ports.outbound.WalletClientPort;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Orquestador SAGA de Transacciones Distribuidas para Cambista Online.
 * Implementa el flujo solicitado:
 * 1. Solicitar cambio (Inicio SAGA)
 * 2. Bloquear saldo origen (Paso 1)
 * 3. Aplicar y garantizar tasa (Paso 2) -> Si falla: Compensar paso 1 (UnlockFunds)
 * 4. Transferencia y Liquidación de saldo destino (Paso 3 y 4) -> Si falla: Compensar
 */
@Service
public class ExchangeOrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ExchangeOrderSagaOrchestrator.class);

    private final ExchangeOrderRepositoryPort orderRepositoryPort;
    private final ExchangeRateClientPort exchangeRateClientPort;
    private final WalletClientPort walletClientPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final OrderNotificationPort orderNotificationPort;
    private final SecureRandom random = new SecureRandom();

    public ExchangeOrderSagaOrchestrator(ExchangeOrderRepositoryPort orderRepositoryPort,
                                         ExchangeRateClientPort exchangeRateClientPort,
                                         WalletClientPort walletClientPort,
                                         OrderEventPublisherPort orderEventPublisherPort,
                                         OrderNotificationPort orderNotificationPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.exchangeRateClientPort = exchangeRateClientPort;
        this.walletClientPort = walletClientPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
        this.orderNotificationPort = orderNotificationPort;
    }

    @Transactional
    public CreateOrderResponse startSaga(CreateOrderRequest request, String userEmail, String userRole) {
        String orderNumber = "TRX-" + (100_000_000 + random.nextInt(900_000_000));
        log.info("[SAGA-ORCHESTRATOR] 🚀 Iniciando SAGA para orden {} usuario={}", orderNumber, userEmail);

        // Paso 0: Cotización inicial
        BigDecimal initialRate = exchangeRateClientPort.getExchangeRate(
                request.getCurrencyOrigin() != null ? request.getCurrencyOrigin().name() : "USD",
                request.getCurrencyDestination() != null ? request.getCurrencyDestination().name() : "PEN",
                request.getOperationType() != null ? request.getOperationType().name() : "COMPRA",
                request.getPointsToRedeem(),
                userEmail,
                userRole
        );

        // ══════════════════════════════════════════════════════════════
        // PASO 1: Bloquear Saldo Origen en wallet-ledger-service
        // ══════════════════════════════════════════════════════════════
        String origCurrency = request.getCurrencyOrigin() != null ? request.getCurrencyOrigin().name() : "USD";
        LockFundsCommand lockCommand = new LockFundsCommand(
                orderNumber,
                userEmail,
                origCurrency,
                request.getAmountSent(),
                "Bloqueo preventivo de fondos para orden " + orderNumber
        );

        LockFundsResult lockResult = walletClientPort.lockFunds(lockCommand);
        if (!lockResult.success()) {
            log.error("[SAGA-ORCHESTRATOR] ❌ Fallo en Paso 1 (Bloqueo): {}. Cancelando SAGA.", lockResult.message());
            throw new IllegalStateException("Fallo al bloquear saldo en billetera: " + lockResult.message());
        }
        log.info("[SAGA-ORCHESTRATOR] ✅ Paso 1 completado: Fondos bloqueados exitosamente para trx={}", orderNumber);

        // ══════════════════════════════════════════════════════════════
        // PASO 2: Aplicar y Validar Tasa en exchange-rate-service
        // ══════════════════════════════════════════════════════════════
        String destCurrency = request.getCurrencyDestination() != null ? request.getCurrencyDestination().name() : "PEN";
        ValidateRateQuery rateQuery = new ValidateRateQuery(
                orderNumber,
                origCurrency,
                destCurrency,
                initialRate,
                request.getPointsToRedeem()
        );

        ValidateRateResult rateResult = exchangeRateClientPort.validateRate(rateQuery);
        if (!rateResult.isValid()) {
            log.warn("[SAGA-ORCHESTRATOR] ⚠️ Fallo en Paso 2 (Tasa de cambio): {}. Ejecutando compensación...", rateResult.reason());
            // COMPENSACIÓN PASO 1: Liberar los fondos bloqueados
            compensateLockFunds(orderNumber, userEmail, origCurrency, request.getAmountSent(), "Tasa de cambio no garantizada o expirada");
            throw new IllegalStateException("No se pudo garantizar la tasa de cambio: " + rateResult.reason());
        }
        log.info("[SAGA-ORCHESTRATOR] ✅ Paso 2 completado: Tasa validada ({}) para trx={}", rateResult.currentRate(), orderNumber);

        // Crear la orden en base de datos local
        ExchangeOrder order = ExchangeOrder.create(
                orderNumber,
                request.getOperationType(),
                request.getCurrencyOrigin(),
                request.getCurrencyDestination(),
                request.getAmountSent(),
                rateResult.currentRate() != null ? rateResult.currentRate() : initialRate,
                request.getPointsToRedeem(),
                userEmail,
                userRole
        );

        ExchangeOrder savedOrder = orderRepositoryPort.save(order);

        // Publicar evento al clúster de Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getOrderNumber(),
                savedOrder.getUserEmail(),
                savedOrder.getOperationType(),
                savedOrder.getCurrencyOrigin(),
                savedOrder.getCurrencyDestination(),
                savedOrder.getAmountSent(),
                savedOrder.getAmountReceived(),
                savedOrder.getExchangeRate()
        );
        orderEventPublisherPort.publishOrderCreated(event);
        orderNotificationPort.notifyOrderCreated(event);

        log.info("[SAGA-ORCHESTRATOR] ✅ SAGA en espera de transferencia bancaria del usuario. Orden {}", orderNumber);

        return new CreateOrderResponse(
                savedOrder.getOrderNumber(),
                savedOrder.getStatus().name(),
                savedOrder.getExchangeRate(),
                savedOrder.getAmountSent(),
                savedOrder.getAmountReceived(),
                savedOrder.getExpiresAt(),
                "REALIZAR_TRANSFERENCIA"
        );
    }

    /**
     * Paso 4: Liquidación y Acreditación de saldo destino tras confirmación de transferencia.
     */
    @Transactional
    public void completeSaga(ExchangeOrder order, int pointsAwarded) {
        log.info("[SAGA-ORCHESTRATOR] 💳 Ejecutando Paso 4 (Liquidación y Acreditación) para orden {}", order.getOrderNumber());

        SettleFundsCommand settleCommand = new SettleFundsCommand(
                order.getOrderNumber(),
                order.getUserEmail(),
                order.getCurrencyOrigin().name(),
                order.getAmountSent(),
                order.getCurrencyDestination().name(),
                order.getAmountReceived(),
                order.getPointsRedeemed(),
                pointsAwarded
        );

        SettleFundsResult settleResult = walletClientPort.settleFunds(settleCommand);
        if (!settleResult.success()) {
            log.error("[SAGA-ORCHESTRATOR] ❌ Error en Paso 4: {}. Ejecutando compensación crítica...", settleResult.message());
            compensateLockFunds(order.getOrderNumber(), order.getUserEmail(), order.getCurrencyOrigin().name(),
                    order.getAmountSent(), "Error crítico durante la liquidación de fondos destino");
            order.markAsCancelled();
            orderRepositoryPort.save(order);
            throw new IllegalStateException("Fallo en liquidación de fondos: " + settleResult.message());
        }

        order.markAsCompleted();
        orderRepositoryPort.save(order);
        log.info("[SAGA-ORCHESTRATOR] 🎉 SAGA COMPLETADA EXITOSAMENTE para orden {}", order.getOrderNumber());
    }

    /**
     * Acción Compensatoria Explícita: Desbloqueo de saldo retenido.
     */
    public void compensateLockFunds(String orderNumber, String userEmail, String currency, BigDecimal amount, String reason) {
        log.warn("[SAGA-COMPENSACIÓN] 🔄 Ejecutando compensación explícita de bloqueo para trx={}", orderNumber);
        UnlockFundsCommand unlockCommand = new UnlockFundsCommand(
                orderNumber,
                userEmail,
                currency,
                amount,
                reason
        );

        UnlockFundsResult unlockResult = walletClientPort.unlockFunds(unlockCommand);
        if (unlockResult.success()) {
            log.info("[SAGA-COMPENSACIÓN] ✅ Compensación completada: Fondos liberados satisfactoriamente para trx={}", orderNumber);
        } else {
            log.error("[SAGA-COMPENSACIÓN] 🚨 ALERTA CRÍTICA: Fallo al compensar fondos para trx={}: {}", orderNumber, unlockResult.message());
        }
    }
}
