package com.cambistaonline.order.application.dto;

import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.OperationType;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

public class CreateOrderRequest {

    @JsonAlias({"originCurrency", "currencyOrigin"})
    private CurrencyType currencyOrigin = CurrencyType.USD;

    @JsonAlias({"targetCurrency", "currencyDestination"})
    private CurrencyType currencyDestination = CurrencyType.PEN;

    @JsonAlias({"operationType", "tipoOperacion"})
    private OperationType operationType = OperationType.COMPRA;

    @JsonAlias({"amount", "amountSent", "montoOrigen"})
    private BigDecimal amountSent = BigDecimal.valueOf(100.00);

    @JsonAlias({"pointsToRedeem", "puntosCanjeados"})
    private int pointsToRedeem = 0;

    public CreateOrderRequest() {}

    public CreateOrderRequest(CurrencyType currencyOrigin, CurrencyType currencyDestination,
                              OperationType operationType, BigDecimal amountSent, int pointsToRedeem) {
        this.currencyOrigin = currencyOrigin != null ? currencyOrigin : CurrencyType.USD;
        this.currencyDestination = currencyDestination != null ? currencyDestination : CurrencyType.PEN;
        this.operationType = operationType != null ? operationType : OperationType.COMPRA;
        this.amountSent = amountSent != null ? amountSent : BigDecimal.valueOf(100.00);
        this.pointsToRedeem = pointsToRedeem;
    }

    public CurrencyType getCurrencyOrigin() { return currencyOrigin; }
    public void setCurrencyOrigin(CurrencyType currencyOrigin) { this.currencyOrigin = currencyOrigin; }
    public void setOriginCurrency(CurrencyType originCurrency) { this.currencyOrigin = originCurrency; }

    public CurrencyType getCurrencyDestination() { return currencyDestination; }
    public void setCurrencyDestination(CurrencyType currencyDestination) { this.currencyDestination = currencyDestination; }
    public void setTargetCurrency(CurrencyType targetCurrency) { this.currencyDestination = targetCurrency; }

    public OperationType getOperationType() { return operationType; }
    public void setOperationType(Object op) {
        if (op instanceof String str) {
            this.operationType = OperationType.from(str);
        } else if (op instanceof OperationType ot) {
            this.operationType = ot;
        }
    }

    public BigDecimal getAmountSent() { return amountSent; }
    public void setAmountSent(BigDecimal amountSent) { this.amountSent = amountSent; }
    public void setAmount(BigDecimal amount) { this.amountSent = amount; }

    public int getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(int pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}
