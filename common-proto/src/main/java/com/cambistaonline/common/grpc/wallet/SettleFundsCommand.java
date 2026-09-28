package com.cambistaonline.common.grpc.wallet;

import java.math.BigDecimal;

/**
 * SettleFundsCommand: Comando inmutable de LIQUIDACIÓN FINAL en el Libro Mayor.
 * Se envía cuando la orden de cambio fue exitosa:
 * 1. Debita definitivamente la moneda origen (ej: -100 USD)
 * 2. Acredita la moneda destino calculada (ej: +375 PEN)
 * 3. Actualiza el balance de puntos de fidelidad (descuenta puntos canjeados y suma cashback)
 */
public record SettleFundsCommand(
        String transactionId,        // ID de la orden procesada
        String userEmail,            // Email del usuario
        String originCurrency,       // Moneda que el usuario entregó (ej: "USD")
        BigDecimal originAmount,     // Monto entregado (ej: 100.00)
        String destinationCurrency,  // Moneda que el usuario recibe (ej: "PEN")
        BigDecimal destinationAmount,// Monto recibido (ej: 375.00)
        int pointsRedeemed,          // Puntos canjeados en esta orden
        int pointsRewarded           // Puntos nuevos ganados por la operación
) {}

