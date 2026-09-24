package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalTime;

public class HourlyAdjustmentStrategy implements ExchangeRateCalculationStrategy {
    private final ExchangeRuleRepositoryPort ruleRepositoryPort;

    public HourlyAdjustmentStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        this.ruleRepositoryPort = ruleRepositoryPort;
    }

    @Override
    public void calculate(CalculationContext context) {
        LocalTime currentTime = context.getRequestTimestamp().toLocalTime();

        BigDecimal adjustment = ruleRepositoryPort.findHourlyAdjustment(
                context.getUserRole(),
                currentTime,
                context.getOperationType()
        ).orElse(BigDecimal.ZERO);

        context.setHourlyAdjustment(adjustment);
        context.addDetail("Ajuste Horario", adjustment);
    }

    @Override
    public int getOrder() {
        return 300;
    }
}
