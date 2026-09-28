package com.cambistaonline.common.grpc.rate;

import java.math.BigDecimal;

/**
 * ValidateRateQuery: Consulta inmutable enviada al microservicio de cotizaciones
 * (exchange-rate-service) para verificar si el precio prometido al usuario sigue vigente.
 *
 * ¿QUÉ ES UN 'record' EN JAVA (Java 14+)?
 * Un 'record' es una estructura concisa pensada para DTOs (Data Transfer Objects).
 * Con una sola línea, el compilador de Java genera automáticamente:
 * - Todos los atributos privados y finales (inmutables, no pueden cambiar).
 * - El constructor con todos los parámetros.
 * - Los métodos de lectura: quoteId(), currencyOrigin(), etc. (en vez de getQuoteId()).
 * - Métodos equals(), hashCode() y toString() para imprimir en consola.
 */
public record ValidateRateQuery(
        String quoteId,             // ID de la cotización que el usuario vio en la pantalla
        String currencyOrigin,      // Moneda entregada (ej: "USD")
        String currencyDestination, // Moneda esperada (ej: "PEN")
        BigDecimal expectedRate,    // Tasa prometida (ej: 3.750)
        int pointsToRedeem          // Puntos del programa de fidelidad a canjear para mejorar el spread
) {}

