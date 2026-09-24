package com.cambistaonline.order.application.service;

import com.cambistaonline.order.application.dto.CreateOrderRequest;
import com.cambistaonline.order.application.dto.CreateOrderResponse;
import com.cambistaonline.order.application.ports.inbound.CreateExchangeOrderUseCase;
import com.cambistaonline.order.application.ports.outbound.ExchangeRateClientPort;
import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;

import java.math.BigDecimal;
import java.security.SecureRandom;

public class CreateExchangeOrderService implements CreateExchangeOrderUseCase {

    private final ExchangeOrderSagaOrchestrator sagaOrchestrator;

    public CreateExchangeOrderService(ExchangeOrderSagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Override
    public CreateOrderResponse execute(CreateOrderRequest request, String userEmail, String userRole) {
        return sagaOrchestrator.startSaga(request, userEmail, userRole);
    }
}
