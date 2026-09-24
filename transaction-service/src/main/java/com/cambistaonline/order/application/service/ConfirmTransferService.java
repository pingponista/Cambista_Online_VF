package com.cambistaonline.order.application.service;

import com.cambistaonline.order.application.dto.OrderSummaryDto;
import com.cambistaonline.order.application.ports.inbound.ConfirmTransferUseCase;
import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.domain.events.OrderCompletedEvent;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;

import java.util.Optional;

public class ConfirmTransferService implements ConfirmTransferUseCase {

    private final ExchangeOrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final OrderNotificationPort orderNotificationPort;
    private final ExchangeOrderSagaOrchestrator sagaOrchestrator;

    public ConfirmTransferService(ExchangeOrderRepositoryPort orderRepositoryPort,
                                  OrderEventPublisherPort orderEventPublisherPort,
                                  OrderNotificationPort orderNotificationPort,
                                  ExchangeOrderSagaOrchestrator sagaOrchestrator) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
        this.orderNotificationPort = orderNotificationPort;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Override
    public OrderSummaryDto execute(String orderNumber, String userEmail, String transactionNumber) {
        Optional<ExchangeOrder> optionalOrder = orderRepositoryPort.findByOrderNumber(orderNumber);

        if (optionalOrder.isEmpty()) {
            throw new IllegalArgumentException(
                    "ORDER_NOT_FOUND: Orden de cambio no encontrada para " + orderNumber);
        }

        ExchangeOrder order = optionalOrder.get();

        // ── SAGA PASO 4: Liquidar fondos multimoneda y abonar saldo destino
        int pointsAwarded = order.getAmountSent().intValue() / 10;
        sagaOrchestrator.completeSaga(order, pointsAwarded);

        ExchangeOrder saved = orderRepositoryPort.findByOrderNumber(orderNumber).orElse(order);

        // ── Publicar evento de dominio → Kafka (auditoría inmutable)
        OrderCompletedEvent event = new OrderCompletedEvent(
                saved.getOrderNumber(),
                saved.getUserEmail(),
                saved.getOperationType(),
                saved.getCurrencyOrigin(),
                saved.getCurrencyDestination(),
                saved.getAmountSent(),
                saved.getAmountReceived(),
                saved.getExchangeRate()
        );
        orderEventPublisherPort.publishOrderCompleted(event);

        // ── Enviar notificación → RabbitMQ (email de confirmación simulado)
        orderNotificationPort.notifyOrderCompleted(event);

        return new OrderSummaryDto(
                saved.getOrderNumber(),
                saved.getOperationType(),
                saved.getCurrencyOrigin(),
                saved.getCurrencyDestination(),
                saved.getAmountSent(),
                saved.getAmountReceived(),
                saved.getExchangeRate(),
                saved.getPointsRedeemed(),
                saved.getStatus().name(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }
}
