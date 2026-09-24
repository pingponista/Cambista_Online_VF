package com.cambistaonline.order.domain.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_UPLOADED,
    IN_VERIFICATION,
    COMPLETED,
    EXPIRED,
    CANCELLED
}
