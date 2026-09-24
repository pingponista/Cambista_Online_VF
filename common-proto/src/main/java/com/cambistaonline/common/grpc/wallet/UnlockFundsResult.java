package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

public record UnlockFundsResult(
        boolean success,
        String transactionId,
        String message,
        BigDecimal restoredAvailable
) {
    public static UnlockFundsResult success(String transactionId, BigDecimal restoredAvailable) {
        return new UnlockFundsResult(true, transactionId, "Compensación ejecutada: Fondos desbloqueados exitosamente", restoredAvailable);
    }

    public static UnlockFundsResult failure(String transactionId, String message) {
        return new UnlockFundsResult(false, transactionId, message, BigDecimal.ZERO);
    }
}
