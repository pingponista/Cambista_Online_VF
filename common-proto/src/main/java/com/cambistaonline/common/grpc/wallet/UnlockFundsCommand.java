package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

/**
 * UnlockFundsCommand: Comando inmutable de COMPENSACIÓN en el patrón SAGA.
 * Se envía a wallet-ledger-service cuando una orden falló o fue cancelada,
 * ordenando que el saldo retenido previamente sea devuelto a "disponible".
 */
public record UnlockFundsCommand(
        String transactionId,     // ID de la orden fallida
        String userEmail,         // Email del dueño de la billetera
        String currency,          // Moneda retenida a liberar (ej: "USD")
        BigDecimal amount,        // Monto a desbloquear
        String compensationReason // Motivo técnico de la compensación (ej: "Error de liquidación bancaria")
) {}

