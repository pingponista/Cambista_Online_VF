package com.cambistaonline.order.application.dto;

import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSummaryDto {
    private String operationId;
    private OperationType operationType;
    private CurrencyType currencyOrigin;
    private CurrencyType currencyDestination;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal exchangeRate;
    private int pointsRedeemed;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public OrderSummaryDto() {}

    public OrderSummaryDto(String operationId, OperationType operationType, CurrencyType currencyOrigin,
                           CurrencyType currencyDestination, BigDecimal amountSent, BigDecimal amountReceived,
                           BigDecimal exchangeRate, int pointsRedeemed, String status,
                           LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.operationId = operationId;
        this.operationType = operationType;
        this.currencyOrigin = currencyOrigin;
        this.currencyDestination = currencyDestination;
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.exchangeRate = exchangeRate;
        this.pointsRedeemed = pointsRedeemed;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getOperationId() { return operationId; }
    public OperationType getOperationType() { return operationType; }
    public CurrencyType getCurrencyOrigin() { return currencyOrigin; }
    public CurrencyType getCurrencyDestination() { return currencyDestination; }
    public BigDecimal getAmountSent() { return amountSent; }
    public BigDecimal getAmountReceived() { return amountReceived; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public int getPointsRedeemed() { return pointsRedeemed; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
