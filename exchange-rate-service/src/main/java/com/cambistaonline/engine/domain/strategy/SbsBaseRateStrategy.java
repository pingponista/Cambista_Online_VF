package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;
import com.cambistaonline.engine.domain.ports.ExchangeRateRepositoryPort;

import java.math.BigDecimal;

public class SbsBaseRateStrategy implements ExchangeRateCalculationStrategy {
    private final ExchangeRateRepositoryPort rateRepositoryPort;

    public SbsBaseRateStrategy(ExchangeRateRepositoryPort rateRepositoryPort) {
        this.rateRepositoryPort = rateRepositoryPort;
    }

    @Override
    public void calculate(CalculationContext context) {
        BigDecimal baseRate = rateRepositoryPort.findActiveBaseRate(
                context.getCurrencyOrigin(),
                context.getCurrencyDestination(),
                context.getOperationType()
        ).orElse(BigDecimal.valueOf(3.7565)); // Fallback rate if none configured

        context.setBaseSbsRate(baseRate);
        context.addDetail("TC Base SBS", baseRate);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
