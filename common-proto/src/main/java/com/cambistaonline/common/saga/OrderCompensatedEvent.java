package com.cambistaonline.common.saga;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCompensatedEvent {

    private String eventId;
    private String orderNumber;
    private String userEmail;
    private String currencyOrigin;
    private BigDecimal amountUnlocked;
    private String reason;
    private LocalDateTime occurredAt;

    public OrderCompensatedEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }

    public OrderCompensatedEvent(String orderNumber, String userEmail, String currencyOrigin,
                                 BigDecimal amountUnlocked, String reason) {
        this.eventId = UUID.randomUUID().toString();
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.currencyOrigin = currencyOrigin;
        this.amountUnlocked = amountUnlocked;
        this.reason = reason;
        this.occurredAt = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getCurrencyOrigin() { return currencyOrigin; }
    public void setCurrencyOrigin(String currencyOrigin) { this.currencyOrigin = currencyOrigin; }
    public BigDecimal getAmountUnlocked() { return amountUnlocked; }
    public void setAmountUnlocked(BigDecimal amountUnlocked) { this.amountUnlocked = amountUnlocked; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
