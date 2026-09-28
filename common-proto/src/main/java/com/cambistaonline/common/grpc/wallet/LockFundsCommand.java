package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

/**
 * LockFundsCommand: Comando inmutable enviado al microservicio de billeteras
 * (wallet-ledger-service) para RETENER preventivamente saldo en la cuenta del usuario.
 *
 * ¿POR QUÉ BLOQUEAMOS SALDO ANTES DE CAMBIAR DIVISAS?
 * Para evitar el problema de "Doble Gasto" (Double-Spending): si el usuario tiene $100 USD,
 * no debe poder crear dos órdenes simultáneas de $100 cada una. Al crear la orden,
 * congelamos los $100 para garantizar que estén disponibles hasta que la orden se complete o cancele.
 */
public record LockFundsCommand(
        String transactionId, // ID único de la orden que solicita el bloqueo
        String userEmail,     // Usuario propietario de la billetera
        String currency,      // Moneda a retener (ej: "USD")
        BigDecimal amount,    // Monto exacto a retener (ej: 100.00)
        String reason         // Razón del bloqueo (ej: "Retención preventiva para orden ORD-01")
) {}

