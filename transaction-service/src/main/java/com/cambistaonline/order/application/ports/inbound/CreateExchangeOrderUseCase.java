package com.cambistaonline.order.application.ports.inbound;

import com.cambistaonline.order.application.dto.CreateOrderRequest;
import com.cambistaonline.order.application.dto.CreateOrderResponse;

public interface CreateExchangeOrderUseCase {
    CreateOrderResponse execute(CreateOrderRequest request, String userEmail, String userRole);
}
