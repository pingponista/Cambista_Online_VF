package com.cambistaonline.order.infrastructure.config;

import com.cambistaonline.order.application.ports.inbound.ConfirmTransferUseCase;
import com.cambistaonline.order.application.ports.inbound.CreateExchangeOrderUseCase;
import com.cambistaonline.order.application.ports.inbound.GetMyOrdersUseCase;
import com.cambistaonline.order.application.ports.outbound.ExchangeRateClientPort;
import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.application.service.ConfirmTransferService;
import com.cambistaonline.order.application.service.CreateExchangeOrderService;
import com.cambistaonline.order.application.service.ExchangeOrderSagaOrchestrator;
import com.cambistaonline.order.application.service.GetMyOrdersService;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBeanConfig {

    @Bean
    public CreateExchangeOrderUseCase createExchangeOrderUseCase(ExchangeOrderSagaOrchestrator sagaOrchestrator) {
        return new CreateExchangeOrderService(sagaOrchestrator);
    }

    @Bean
    public GetMyOrdersUseCase getMyOrdersUseCase(ExchangeOrderRepositoryPort orderRepositoryPort) {
        return new GetMyOrdersService(orderRepositoryPort);
    }

    @Bean
    public ConfirmTransferUseCase confirmTransferUseCase(
            ExchangeOrderRepositoryPort orderRepositoryPort,
            OrderEventPublisherPort orderEventPublisherPort,
            OrderNotificationPort orderNotificationPort,
            ExchangeOrderSagaOrchestrator sagaOrchestrator) {
        return new ConfirmTransferService(
                orderRepositoryPort,
                orderEventPublisherPort,
                orderNotificationPort,
                sagaOrchestrator
        );
    }
}
