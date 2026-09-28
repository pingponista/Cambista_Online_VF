package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

/**
 * UnlockFundsResult: Resultado inmutable devuelto tras ejecutar la acción de compensación.
 */
public record UnlockFundsResult(
        boolean success,              // ¿Se desbloqueó correctamente el dinero?
        String transactionId,         // ID de transacción
        String message,               // Mensaje ("Compensación ejecutada con éxito")
        BigDecimal restoredAvailable  // Nuevo saldo disponible tras devolver el dinero retenido
) {
    // Fábrica estática para compensación exitosa
    public static UnlockFundsResult success(String transactionId, BigDecimal restoredAvailable) {
        return new UnlockFundsResult(true, transactionId, "Compensación ejecutada: Fondos desbloqueados exitosamente", restoredAvailable);
    }

    // Fábrica estática para reportar fallo en compensación
    public static UnlockFundsResult failure(String transactionId, String message) {
        return new UnlockFundsResult(false, transactionId, message, BigDecimal.ZERO);
    }
}

