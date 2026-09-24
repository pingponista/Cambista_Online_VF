package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;

public interface ExchangeRateCalculationStrategy {
    void calculate(CalculationContext context);
    int getOrder();
}
