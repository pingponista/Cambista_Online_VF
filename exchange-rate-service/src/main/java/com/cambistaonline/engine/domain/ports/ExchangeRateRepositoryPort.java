package com.cambistaonline.engine.domain.ports;

import com.cambistaonline.engine.domain.model.CurrencyType;
import com.cambistaonline.engine.domain.model.OperationType;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateRepositoryPort {
    Optional<BigDecimal> findActiveBaseRate(CurrencyType origin, CurrencyType destination, OperationType operation);
}
