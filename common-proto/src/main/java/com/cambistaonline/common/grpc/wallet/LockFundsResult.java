package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

/**
 * LockFundsResult: Resultado inmutable devuelto por wallet-ledger-service tras intentar
 * retener los fondos. Si el usuario no tiene saldo suficiente, success será false.
 */
public record LockFundsResult(
        boolean success,              // ¿Se pudo bloquear el saldo?
        String transactionId,         // ID de transacción asociado
        String message,               // Mensaje explicativo ("Fondos bloqueados" o "Saldo insuficiente")
        BigDecimal remainingAvailable // Saldo libre restante que le queda al usuario tras la retención
) {
    // Fábrica estática para responder cuando el bloqueo fue exitoso
    public static LockFundsResult success(String transactionId, BigDecimal remainingAvailable) {
        return new LockFundsResult(true, transactionId, "Fondos bloqueados exitosamente", remainingAvailable);
    }

    // Fábrica estática para responder cuando falló (ej: fondos insuficientes)
    public static LockFundsResult failure(String transactionId, String message) {
        return new LockFundsResult(false, transactionId, message, BigDecimal.ZERO);
    }
}

