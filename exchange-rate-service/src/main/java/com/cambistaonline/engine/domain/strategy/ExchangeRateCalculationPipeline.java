package com.cambistaonline.engine.domain.strategy;

import com.cambistaonline.engine.domain.model.CalculationContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExchangeRateCalculationPipeline {
    private final List<ExchangeRateCalculationStrategy> strategies;

    public ExchangeRateCalculationPipeline(List<ExchangeRateCalculationStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
        this.strategies.sort(Comparator.comparingInt(ExchangeRateCalculationStrategy::getOrder));
    }

    public CalculationContext execute(CalculationContext context) {
        for (ExchangeRateCalculationStrategy strategy : strategies) {
            strategy.calculate(context);
        }
        context.recalculateFinalRate();
        return context;
    }
}
