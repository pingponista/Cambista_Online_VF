package com.cambistaonline.common.grpc.rate;

import java.math.BigDecimal;

public record ValidateRateQuery(
        String quoteId,
        String currencyOrigin,
        String currencyDestination,
        BigDecimal expectedRate,
        int pointsToRedeem
) {}
