package com.cambistaonline.common.grpc.rate;

import java.math.BigDecimal;

/**
 * ValidateRateResult: Respuesta inmutable devuelta por el motor de cotización.
 * Indica si la tasa sigue vigente o si caducó/cambió en el mercado.
 *
 * CONCEPTOS JAVA CLAVE:
 * - 'public static ValidateRateResult valid(...)': Patrón Static Factory Method.
 *   En vez de hacer 'new ValidateRateResult(true, ...)' en todo el código,
 *   proveemos un método semántico y legible 'ValidateRateResult.valid(...)'.
 */
public record ValidateRateResult(
        boolean isValid,          // ¿Es válida la tasa? (true = sí, false = no)
        String quoteId,           // ID de cotización verificado
        BigDecimal currentRate,   // Tasa actual de mercado
        String reason             // Explicación textual del resultado
) {
    // Fábrica estática para crear rápidamente una respuesta de éxito
    public static ValidateRateResult valid(String quoteId, BigDecimal currentRate) {
        return new ValidateRateResult(true, quoteId, currentRate, "Tasa de cambio confirmada");
    }

    // Fábrica estática para crear rápidamente una respuesta de rechazo
    public static ValidateRateResult invalid(String quoteId, BigDecimal currentRate, String reason) {
        return new ValidateRateResult(false, quoteId, currentRate, reason);
    }
}

