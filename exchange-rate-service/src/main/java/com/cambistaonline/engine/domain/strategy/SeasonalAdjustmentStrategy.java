package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;

import java.math.BigDecimal;

public class SeasonalAdjustmentStrategy implements ExchangeRateCalculationStrategy {
    private final ExchangeRuleRepositoryPort ruleRepositoryPort;

    public SeasonalAdjustmentStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        this.ruleRepositoryPort = ruleRepositoryPort;
    }

    @Override
    public void calculate(CalculationContext context) {
        BigDecimal adjustment = ruleRepositoryPort.findSeasonalAdjustment(
                context.getUserRole(),
                context.getRequestTimestamp(),
                context.getOperationType()
        ).orElse(BigDecimal.ZERO);

        context.setSeasonalAdjustment(adjustment);
        context.addDetail("Ajuste Estacional", adjustment);
    }

    @Override
    public int getOrder() {
        return 400;
    }
}
