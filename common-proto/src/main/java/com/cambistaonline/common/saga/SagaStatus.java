package com.cambistaonline.common.saga;

public enum SagaStatus {
    SAGA_STARTED,
    FUNDS_LOCKED,
    RATE_VALIDATED,
    PAYMENT_CONFIRMED,
    SETTLED,
    COMPENSATION_REQUESTED,
    COMPENSATED,
    FAILED
}
