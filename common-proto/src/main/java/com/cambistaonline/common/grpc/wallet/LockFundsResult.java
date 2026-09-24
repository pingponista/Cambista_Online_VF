package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

public record LockFundsResult(
        boolean success,
        String transactionId,
        String message,
        BigDecimal remainingAvailable
) {
    public static LockFundsResult success(String transactionId, BigDecimal remainingAvailable) {
        return new LockFundsResult(true, transactionId, "Fondos bloqueados exitosamente", remainingAvailable);
    }

    public static LockFundsResult failure(String transactionId, String message) {
        return new LockFundsResult(false, transactionId, message, BigDecimal.ZERO);
    }
}
