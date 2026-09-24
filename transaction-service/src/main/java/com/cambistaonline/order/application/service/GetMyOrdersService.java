package com.cambistaonline.order.application.service;

import com.cambistaonline.order.application.dto.OrderSummaryDto;
import com.cambistaonline.order.application.ports.inbound.GetMyOrdersUseCase;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;

import java.util.List;
import java.util.stream.Collectors;

public class GetMyOrdersService implements GetMyOrdersUseCase {
    private final ExchangeOrderRepositoryPort orderRepositoryPort;

    public GetMyOrdersService(ExchangeOrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public List<OrderSummaryDto> execute(String userEmail) {
        List<ExchangeOrder> orders = orderRepositoryPort.findByUserEmail(userEmail);

        return orders.stream()
                .map(o -> new OrderSummaryDto(
                        o.getOrderNumber(),
                        o.getOperationType(),
                        o.getCurrencyOrigin(),
                        o.getCurrencyDestination(),
                        o.getAmountSent(),
                        o.getAmountReceived(),
                        o.getExchangeRate(),
                        o.getPointsRedeemed(),
                        o.getStatus().name(),
                        o.getCreatedAt(),
                        o.getExpiresAt()
                ))
                .collect(Collectors.toList());
    }
}
