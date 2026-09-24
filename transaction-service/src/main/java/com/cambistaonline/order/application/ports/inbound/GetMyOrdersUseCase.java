package com.cambistaonline.order.application.ports.inbound;

import com.cambistaonline.order.application.dto.OrderSummaryDto;
import java.util.List;

public interface GetMyOrdersUseCase {
    List<OrderSummaryDto> execute(String userEmail);
}
