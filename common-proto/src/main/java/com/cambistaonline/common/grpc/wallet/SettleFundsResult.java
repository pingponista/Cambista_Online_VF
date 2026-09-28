package com.cambistaonline.common.grpc.wallet;

/**
 * SettleFundsResult: Resultado inmutable devuelto por el Ledger contable
 * tras asentar la partida doble de la orden de cambio.
 */
public record SettleFundsResult(
        boolean success,      // ¿Se asentaron los asientos contables correctamente?
        String transactionId, // ID de la transacción
        String message        // Mensaje de auditoría
) {
    // Fábrica estática para éxito en liquidación
    public static SettleFundsResult success(String transactionId) {
        return new SettleFundsResult(true, transactionId, "Liquidación completada en Ledger exitosamente");
    }

    // Fábrica estática para reportar fallo en liquidación
    public static SettleFundsResult failure(String transactionId, String message) {
        return new SettleFundsResult(false, transactionId, message);
    }
}

