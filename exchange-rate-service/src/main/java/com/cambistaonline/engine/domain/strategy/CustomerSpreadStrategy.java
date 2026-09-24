package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;

import java.math.BigDecimal;

public class CustomerSpreadStrategy implements ExchangeRateCalculationStrategy {
    private final ExchangeRuleRepositoryPort ruleRepositoryPort;

    public CustomerSpreadStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        this.ruleRepositoryPort = ruleRepositoryPort;
    }

    @Override
    public void calculate(CalculationContext context) {
        BigDecimal spread = ruleRepositoryPort.findSpread(
                context.getUserRole(),
                context.getCustomerLevel(),
                context.getOperationType()
        ).orElse(BigDecimal.valueOf(0.0100)); // Default spread

        context.setSpread(spread);
        String label = "Spread " + capitalize(context.getCustomerLevel().name());
        context.addDetail(label, spread);
    }

    @Override
    public int getOrder() {
        return 200;
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
