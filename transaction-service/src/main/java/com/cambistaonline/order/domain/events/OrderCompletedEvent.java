package com.cambistaonline.order.domain.events;

import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de dominio inmutable publicado cuando el operador confirma
 * que la transferencia bancaria fue recibida y la orden queda COMPLETADA.
 */
public class OrderCompletedEvent {

    private String orderNumber;
    private String userEmail;
    private OperationType operationType;
    private CurrencyType currencyOrigin;
    private CurrencyType currencyDestination;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal exchangeRate;
    private LocalDateTime occurredAt;

    public OrderCompletedEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    public OrderCompletedEvent(String orderNumber, String userEmail,
                             OperationType operationType,
                             CurrencyType currencyOrigin, CurrencyType currencyDestination,
                             BigDecimal amountSent, BigDecimal amountReceived,
                             BigDecimal exchangeRate) {
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.operationType = operationType;
        this.currencyOrigin = currencyOrigin;
        this.currencyDestination = currencyDestination;
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.exchangeRate = exchangeRate;
        this.occurredAt = LocalDateTime.now();
    }

    public String getOrderNumber()             { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getUserEmail()               { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public OperationType getOperationType()    { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }

    public CurrencyType getCurrencyOrigin()    { return currencyOrigin; }
    public void setCurrencyOrigin(CurrencyType currencyOrigin) { this.currencyOrigin = currencyOrigin; }

    public CurrencyType getCurrencyDestination(){ return currencyDestination; }
    public void setCurrencyDestination(CurrencyType currencyDestination) { this.currencyDestination = currencyDestination; }

    public BigDecimal getAmountSent()          { return amountSent; }
    public void setAmountSent(BigDecimal amountSent) { this.amountSent = amountSent; }

    public BigDecimal getAmountReceived()      { return amountReceived; }
    public void setAmountReceived(BigDecimal amountReceived) { this.amountReceived = amountReceived; }

    public BigDecimal getExchangeRate()        { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public LocalDateTime getOccurredAt()       { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
