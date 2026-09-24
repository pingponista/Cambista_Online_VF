package com.cambistaonline.engine.domain.ports;

import com.cambistaonline.engine.domain.model.CustomerLevel;
import com.cambistaonline.engine.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public interface ExchangeRuleRepositoryPort {
    Optional<BigDecimal> findSpread(String userRole, CustomerLevel level, OperationType operation);
    Optional<BigDecimal> findHourlyAdjustment(String userRole, LocalTime time, OperationType operation);
    Optional<BigDecimal> findSeasonalAdjustment(String userRole, LocalDateTime dateTime, OperationType operation);
}
