package com.cambistaonline.order.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class ExchangeOrder {
    private Long id;
    private final String orderNumber;
    private final OperationType operationType;
    private final CurrencyType currencyOrigin;
    private final CurrencyType currencyDestination;
    private final BigDecimal amountSent;
    private final BigDecimal amountReceived;
    private final BigDecimal exchangeRate;
    private final int pointsRedeemed;
    private OrderStatus status;
    private final String userEmail;
    private final String userRole;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    public ExchangeOrder(Long id, String orderNumber, OperationType operationType,
                         CurrencyType currencyOrigin, CurrencyType currencyDestination,
                         BigDecimal amountSent, BigDecimal amountReceived, BigDecimal exchangeRate,
                         int pointsRedeemed, OrderStatus status, String userEmail, String userRole,
                         LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operationType = operationType;
        this.currencyOrigin = currencyOrigin;
        this.currencyDestination = currencyDestination;
        this.amountSent = amountSent != null ? amountSent.setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
        this.amountReceived = amountReceived != null ? amountReceived.setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
        this.exchangeRate = exchangeRate != null ? exchangeRate.setScale(4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
        this.pointsRedeemed = pointsRedeemed;
        this.status = status != null ? status : OrderStatus.PENDING_PAYMENT;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.expiresAt = expiresAt != null ? expiresAt : LocalDateTime.now().plusMinutes(15);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static ExchangeOrder create(String orderNumber, OperationType operationType,
                                       CurrencyType currencyOrigin, CurrencyType currencyDestination,
                                       BigDecimal amountSent, BigDecimal exchangeRate,
                                       int pointsRedeemed, String userEmail, String userRole) {
        BigDecimal calculatedReceived = amountSent.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);
        LocalDateTime now = LocalDateTime.now();

        return new ExchangeOrder(
                null, orderNumber, operationType, currencyOrigin, currencyDestination,
                amountSent, calculatedReceived, exchangeRate, pointsRedeemed,
                OrderStatus.PENDING_PAYMENT, userEmail, userRole,
                now.plusMinutes(15), now
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUploaded() {
        if (this.status == OrderStatus.PENDING_PAYMENT) {
            this.status = OrderStatus.PAYMENT_UPLOADED;
        }
    }

    public void markAsCompleted() {
        this.status = OrderStatus.COMPLETED;
    }

    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markAsExpired() {
        this.status = OrderStatus.EXPIRED;
    }

    // Getters
    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public OperationType getOperationType() { return operationType; }
    public CurrencyType getCurrencyOrigin() { return currencyOrigin; }
    public CurrencyType getCurrencyDestination() { return currencyDestination; }
    public BigDecimal getAmountSent() { return amountSent; }
    public BigDecimal getAmountReceived() { return amountReceived; }
    public BigDecimal getExchangeRate() { return exchangeRate; }
    public int getPointsRedeemed() { return pointsRedeemed; }
    public OrderStatus getStatus() { return status; }
    public String getUserEmail() { return userEmail; }
    public String getUserRole() { return userRole; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
