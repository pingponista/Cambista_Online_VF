package com.cambistaonline.order.application.ports.outbound;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;

import java.math.BigDecimal;

public interface ExchangeRateClientPort {
    BigDecimal getExchangeRate(String currencyOrigin, String currencyDestination,
                               String operationType, int pointsToRedeem,
                               String userEmail, String userRole);

    ValidateRateResult validateRate(ValidateRateQuery query);
}
