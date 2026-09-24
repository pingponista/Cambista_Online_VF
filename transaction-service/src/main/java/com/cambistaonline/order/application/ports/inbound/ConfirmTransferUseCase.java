package com.cambistaonline.order.application.ports.inbound;

import com.cambistaonline.order.application.dto.OrderSummaryDto;

public interface ConfirmTransferUseCase {
    OrderSummaryDto execute(String orderNumber, String userEmail, String transactionNumber);
}
