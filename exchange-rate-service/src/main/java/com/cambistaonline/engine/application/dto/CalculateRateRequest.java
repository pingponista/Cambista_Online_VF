package com.cambistaonline.engine.application.dto;

import com.cambistaonline.engine.domain.model.CurrencyType;
import com.cambistaonline.engine.domain.model.OperationType;
import com.fasterxml.jackson.annotation.JsonAlias;

public class CalculateRateRequest {

    @JsonAlias({"originCurrency", "currencyOrigin"})
    private CurrencyType currencyOrigin = CurrencyType.USD;

    @JsonAlias({"targetCurrency", "currencyDestination"})
    private CurrencyType currencyDestination = CurrencyType.PEN;

    @JsonAlias({"operationType", "tipoOperacion"})
    private OperationType operationType = OperationType.COMPRA;

    @JsonAlias({"pointsToRedeem", "puntosCanjeados"})
    private int pointsToRedeem = 0;

    public CalculateRateRequest() {}

    public CalculateRateRequest(CurrencyType currencyOrigin, CurrencyType currencyDestination, OperationType operationType, int pointsToRedeem) {
        this.currencyOrigin = currencyOrigin != null ? currencyOrigin : CurrencyType.USD;
        this.currencyDestination = currencyDestination != null ? currencyDestination : CurrencyType.PEN;
        this.operationType = operationType != null ? operationType : OperationType.COMPRA;
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

    public int getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(int pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
}
