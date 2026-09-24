package com.cambistaonline.order.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateOrderResponse {
    private String operationId;
    private String status;
    private BigDecimal exchangeRate;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private LocalDateTime expiresAt;
    private String nextStep;

    public CreateOrderResponse() {}

    public CreateOrderResponse(String operationId, String status, BigDecimal exchangeRate,
                               BigDecimal amountSent, BigDecimal amountReceived,
                               LocalDateTime expiresAt, String nextStep) {
        this.operationId = operationId;
        this.status = status;
        this.exchangeRate = exchangeRate;
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.expiresAt = expiresAt;
        this.nextStep = nextStep;
    }

    public String getOperationId() { return operationId; }
    public String getOrderNumber() { return operationId; }
    public String getStatus() { return status; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public BigDecimal getAmountSent() { return amountSent; }
    public BigDecimal getAmountReceived() { return amountReceived; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public String getNextStep() { return nextStep; }
}
