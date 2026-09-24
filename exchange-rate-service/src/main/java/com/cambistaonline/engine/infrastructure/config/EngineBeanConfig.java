package com.cambistaonline.engine.infrastructure.config;

import com.cambistaonline.engine.application.ports.inbound.CalculateExchangeRateUseCase;
import com.cambistaonline.engine.application.service.CalculateExchangeRateService;
import com.cambistaonline.engine.domain.ports.ExchangeRateRepositoryPort;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;
import com.cambistaonline.engine.domain.ports.UserPointsRepositoryPort;
import com.cambistaonline.engine.domain.strategy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EngineBeanConfig {

    @Bean
    public SbsBaseRateStrategy sbsBaseRateStrategy(ExchangeRateRepositoryPort rateRepositoryPort) {
        return new SbsBaseRateStrategy(rateRepositoryPort);
    }

    @Bean
    public CustomerSpreadStrategy customerSpreadStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        return new CustomerSpreadStrategy(ruleRepositoryPort);
    }

    @Bean
    public HourlyAdjustmentStrategy hourlyAdjustmentStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        return new HourlyAdjustmentStrategy(ruleRepositoryPort);
    }

    @Bean
    public SeasonalAdjustmentStrategy seasonalAdjustmentStrategy(ExchangeRuleRepositoryPort ruleRepositoryPort) {
        return new SeasonalAdjustmentStrategy(ruleRepositoryPort);
    }

    @Bean
    public PointsRedemptionStrategy pointsRedemptionStrategy(UserPointsRepositoryPort pointsRepositoryPort) {
        return new PointsRedemptionStrategy(pointsRepositoryPort);
    }

    @Bean
    public ExchangeRateCalculationPipeline exchangeRateCalculationPipeline(
            SbsBaseRateStrategy sbsBaseRateStrategy,
            CustomerSpreadStrategy customerSpreadStrategy,
            HourlyAdjustmentStrategy hourlyAdjustmentStrategy,
            SeasonalAdjustmentStrategy seasonalAdjustmentStrategy,
            PointsRedemptionStrategy pointsRedemptionStrategy) {

        List<ExchangeRateCalculationStrategy> strategies = List.of(
                sbsBaseRateStrategy,
                customerSpreadStrategy,
                hourlyAdjustmentStrategy,
                seasonalAdjustmentStrategy,
                pointsRedemptionStrategy
        );

        return new ExchangeRateCalculationPipeline(strategies);
    }

    @Bean
    public CalculateExchangeRateUseCase calculateExchangeRateUseCase(
            ExchangeRateCalculationPipeline exchangeRateCalculationPipeline) {
        return new CalculateExchangeRateService(exchangeRateCalculationPipeline);
    }
}
