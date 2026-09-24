package com.cambistaonline.engine.application.dto;

import java.math.BigDecimal;

public class FxBreakdownResponse {
    private int userPointsBalance;
    private BigDecimal baseSbsRate;
    private BigDecimal spreadRate;
    private BigDecimal timeAdjRate;
    private BigDecimal seasonalAdjRate;
    private BigDecimal promoDiscount;
    private String promoName;
    private int earnPointsPerOrder;

    public FxBreakdownResponse() {}

    public FxBreakdownResponse(int userPointsBalance, BigDecimal baseSbsRate, BigDecimal spreadRate,
                               BigDecimal timeAdjRate, BigDecimal seasonalAdjRate, BigDecimal promoDiscount,
                               String promoName, int earnPointsPerOrder) {
        this.userPointsBalance = userPointsBalance;
        this.baseSbsRate = baseSbsRate;
        this.spreadRate = spreadRate;
        this.timeAdjRate = timeAdjRate;
        this.seasonalAdjRate = seasonalAdjRate;
        this.promoDiscount = promoDiscount;
        this.promoName = promoName;
        this.earnPointsPerOrder = earnPointsPerOrder;
    }

    public int getUserPointsBalance() { return userPointsBalance; }
    public BigDecimal getBaseSbsRate() { return baseSbsRate; }
    public BigDecimal getSpreadRate() { return spreadRate; }
    public BigDecimal getTimeAdjRate() { return timeAdjRate; }
    public BigDecimal getSeasonalAdjRate() { return seasonalAdjRate; }
    public BigDecimal getPromoDiscount() { return promoDiscount; }
    public String getPromoName() { return promoName; }
    public int getEarnPointsPerOrder() { return earnPointsPerOrder; }
}
