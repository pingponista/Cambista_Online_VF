package com.cambistaonline.engine.application.service;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;
import com.cambistaonline.engine.application.dto.CalculateRateRequest;
import com.cambistaonline.engine.application.dto.CalculateRateResponse;
import com.cambistaonline.engine.application.ports.inbound.CalculateExchangeRateUseCase;
import com.cambistaonline.engine.application.ports.inbound.ValidateExchangeRateUseCase;
import com.cambistaonline.engine.domain.model.CustomerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ValidateExchangeRateService implements ValidateExchangeRateUseCase {

    private static final Logger log = LoggerFactory.getLogger(ValidateExchangeRateService.class);
    private static final BigDecimal MAX_TOLERANCE = new BigDecimal("0.02"); // 2 centésimos de tolerancia

    private final CalculateExchangeRateUseCase calculateExchangeRateUseCase;

    public ValidateExchangeRateService(CalculateExchangeRateUseCase calculateExchangeRateUseCase) {
        this.calculateExchangeRateUseCase = calculateExchangeRateUseCase;
    }

    @Override
    public ValidateRateResult validateRate(ValidateRateQuery query) {
        log.info("[SAGA RATE] Validando tasa para quoteId={} esperado={}", query.quoteId(), query.expectedRate());

        try {
            CalculateRateRequest req = new CalculateRateRequest();
            if (query.currencyOrigin() != null) {
                req.setCurrencyOrigin(com.cambistaonline.engine.domain.model.CurrencyType.valueOf(query.currencyOrigin().toUpperCase()));
            }
            if (query.currencyDestination() != null) {
                req.setCurrencyDestination(com.cambistaonline.engine.domain.model.CurrencyType.valueOf(query.currencyDestination().toUpperCase()));
            }
            req.setPointsToRedeem(query.pointsToRedeem());
            req.setOperationType("VENTA");

            CalculateRateResponse response = calculateExchangeRateUseCase.execute(
                    req,
                    "system-saga@cambista.com",
                    "Saga Evaluator",
                    "N",
                    CustomerLevel.PREFERENTE
            );

            BigDecimal currentRate = response.getTipoCambio().getTipoCambioFinal();

            if (query.expectedRate() == null) {
                return ValidateRateResult.valid(query.quoteId(), currentRate);
            }

            BigDecimal diff = currentRate.subtract(query.expectedRate()).abs();
            if (diff.compareTo(MAX_TOLERANCE) <= 0) {
                log.info("[SAGA RATE] ✅ Tasa garantizada válida: esperado={} actual={}", query.expectedRate(), currentRate);
                return ValidateRateResult.valid(query.quoteId(), currentRate);
            } else {
                log.warn("[SAGA RATE] ❌ Tasa expirada o desviada: esperado={} actual={} dif={}", query.expectedRate(), currentRate, diff);
                return ValidateRateResult.invalid(
                        query.quoteId(),
                        currentRate,
                        String.format("Tasa expirada o con variación excesiva: cotizada=%s, actual=%s", query.expectedRate(), currentRate)
                );
            }
        } catch (Exception e) {
            log.error("[SAGA RATE] Error calculando validación de tasa: {}", e.getMessage());
            return ValidateRateResult.invalid(query.quoteId(), BigDecimal.ZERO, "Error en motor de cálculo: " + e.getMessage());
        }
    }
}
