package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;
import com.cambistaonline.engine.domain.ports.UserPointsRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointsRedemptionStrategy implements ExchangeRateCalculationStrategy {
    private final UserPointsRepositoryPort pointsRepositoryPort;

    public PointsRedemptionStrategy(UserPointsRepositoryPort pointsRepositoryPort) {
        this.pointsRepositoryPort = pointsRepositoryPort;
    }

    @Override
    public void calculate(CalculationContext context) {
        int availableBalance = pointsRepositoryPort.getUserPointsBalance(context.getUserEmail());
        context.setAvailablePointsBalance(availableBalance);

        int requestedPoints = context.getRequestedPointsToRedeem();
        if (requestedPoints <= 0 || availableBalance <= 0) {
            context.setPointsBenefit(BigDecimal.ZERO);
            context.setActualPointsRedeemed(0);
            context.addDetail("Canje de Puntos", BigDecimal.ZERO);
            return;
        }

        UserPointsRepositoryPort.PointsConfig config = pointsRepositoryPort.findPointsConfig(context.getUserRole())
                .orElse(new UserPointsRepositoryPort.PointsConfig(100, 100, BigDecimal.valueOf(0.0005)));

        // Points cannot exceed available balance
        int pointsToRedeem = Math.min(requestedPoints, availableBalance);

        // Canje mínimo
        if (pointsToRedeem < config.minRedeemable()) {
            context.setPointsBenefit(BigDecimal.ZERO);
            context.setActualPointsRedeemed(0);
            context.addDetail("Canje de Puntos", BigDecimal.ZERO);
            return;
        }

        // Bloques completos de puntos (e.g. 100 puntos = 0.0005 mejora)
        int blocks = pointsToRedeem / config.pointsPerBlock();
        int actualPointsUsed = blocks * config.pointsPerBlock();

        BigDecimal benefitValue = config.rateImprovementPerBlock()
                .multiply(BigDecimal.valueOf(blocks))
                .setScale(4, RoundingMode.HALF_EVEN);

        // Sign adjustments: For VENTA or COMPRA, points improve the rate for the customer
        BigDecimal signedBenefit = benefitValue.negate(); // Discount on final rate

        context.setPointsBenefit(signedBenefit);
        context.setActualPointsRedeemed(actualPointsUsed);
        context.addDetail("Canje de Puntos", signedBenefit);
    }

    @Override
    public int getOrder() {
        return 500;
    }
}
