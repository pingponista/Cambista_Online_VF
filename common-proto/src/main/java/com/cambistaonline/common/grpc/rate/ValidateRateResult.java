package com.cambistaonline.common.grpc.rate;

import java.math.BigDecimal;

public record ValidateRateResult(
        boolean isValid,
        String quoteId,
        BigDecimal currentRate,
        String reason
) {
    public static ValidateRateResult valid(String quoteId, BigDecimal currentRate) {
        return new ValidateRateResult(true, quoteId, currentRate, "Tasa de cambio confirmada");
    }

    public static ValidateRateResult invalid(String quoteId, BigDecimal currentRate, String reason) {
        return new ValidateRateResult(false, quoteId, currentRate, reason);
    }
}
