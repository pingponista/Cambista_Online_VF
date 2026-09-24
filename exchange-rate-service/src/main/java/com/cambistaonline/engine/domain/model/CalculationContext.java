package com.cambistaonline.engine.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculationContext {
    private final String userEmail;
    private final String userName;
    private final String userRole; // "N" or "J"
    private final CustomerLevel customerLevel;
    private final CurrencyType currencyOrigin;
    private final CurrencyType currencyDestination;
    private final OperationType operationType;
    private final int requestedPointsToRedeem;
    private final LocalDateTime requestTimestamp;

    // Calculation state accumulated by Strategies
    private BigDecimal baseSbsRate = BigDecimal.ZERO;
    private BigDecimal spread = BigDecimal.ZERO;
    private BigDecimal hourlyAdjustment = BigDecimal.ZERO;
    private BigDecimal seasonalAdjustment = BigDecimal.ZERO;
    private BigDecimal pointsBenefit = BigDecimal.ZERO;
    private BigDecimal finalRate = BigDecimal.ZERO;

    private int availablePointsBalance = 0;
    private int actualPointsRedeemed = 0;

    private final List<CalculationDetailItem> details = new ArrayList<>();

    public CalculationContext(String userEmail, String userName, String userRole,
                              CustomerLevel customerLevel, CurrencyType currencyOrigin,
                              CurrencyType currencyDestination, OperationType operationType,
                              int requestedPointsToRedeem, LocalDateTime requestTimestamp) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userRole = userRole;
        this.customerLevel = customerLevel != null ? customerLevel : CustomerLevel.NORMAL;
        this.currencyOrigin = currencyOrigin;
        this.currencyDestination = currencyDestination;
        this.operationType = operationType != null ? operationType : OperationType.COMPRA;
        this.requestedPointsToRedeem = requestedPointsToRedeem;
        this.requestTimestamp = requestTimestamp != null ? requestTimestamp : LocalDateTime.now();
    }

    public void addDetail(String concepto, BigDecimal valor) {
        details.add(new CalculationDetailItem(concepto, valor));
    }

    public void recalculateFinalRate() {
        // TC Final = Base SBS + Spread + Ajuste Horario + Ajuste Estacional + Beneficio Puntos
        BigDecimal calculated = baseSbsRate
                .add(spread)
                .add(hourlyAdjustment)
                .add(seasonalAdjustment)
                .add(pointsBenefit);

        this.finalRate = calculated.setScale(4, RoundingMode.HALF_EVEN);
    }

    // Getters and Setters
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public String getUserRole() { return userRole; }
    public CustomerLevel getCustomerLevel() { return customerLevel; }
    public CurrencyType getCurrencyOrigin() { return currencyOrigin; }
    public CurrencyType getCurrencyDestination() { return currencyDestination; }
    public OperationType getOperationType() { return operationType; }
    public int getRequestedPointsToRedeem() { return requestedPointsToRedeem; }
    public LocalDateTime getRequestTimestamp() { return requestTimestamp; }

    public BigDecimal getBaseSbsRate() { return baseSbsRate; }
    public void setBaseSbsRate(BigDecimal baseSbsRate) { this.baseSbsRate = baseSbsRate; }

    public BigDecimal getSpread() { return spread; }
    public void setSpread(BigDecimal spread) { this.spread = spread; }

    public BigDecimal getHourlyAdjustment() { return hourlyAdjustment; }
    public void setHourlyAdjustment(BigDecimal hourlyAdjustment) { this.hourlyAdjustment = hourlyAdjustment; }

    public BigDecimal getSeasonalAdjustment() { return seasonalAdjustment; }
    public void setSeasonalAdjustment(BigDecimal seasonalAdjustment) { this.seasonalAdjustment = seasonalAdjustment; }

    public BigDecimal getPointsBenefit() { return pointsBenefit; }
    public void setPointsBenefit(BigDecimal pointsBenefit) { this.pointsBenefit = pointsBenefit; }

    public BigDecimal getFinalRate() { return finalRate; }

    public int getAvailablePointsBalance() { return availablePointsBalance; }
    public void setAvailablePointsBalance(int availablePointsBalance) { this.availablePointsBalance = availablePointsBalance; }

    public int getActualPointsRedeemed() { return actualPointsRedeemed; }
    public void setActualPointsRedeemed(int actualPointsRedeemed) { this.actualPointsRedeemed = actualPointsRedeemed; }

    public List<CalculationDetailItem> getDetails() {
        return Collections.unmodifiableList(details);
    }
}
