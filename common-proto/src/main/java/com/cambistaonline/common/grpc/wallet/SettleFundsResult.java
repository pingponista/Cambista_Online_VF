package com.cambistaonline.common.grpc.wallet;

public record SettleFundsResult(
        boolean success,
        String transactionId,
        String message
) {
    public static SettleFundsResult success(String transactionId) {
        return new SettleFundsResult(true, transactionId, "Liquidación completada en Ledger exitosamente");
    }

    public static SettleFundsResult failure(String transactionId, String message) {
        return new SettleFundsResult(false, transactionId, message);
    }
}
