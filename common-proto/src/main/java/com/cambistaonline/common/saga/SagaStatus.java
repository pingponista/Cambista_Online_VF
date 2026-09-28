package com.cambistaonline.common.saga;

/**
 * SagaStatus: Enumeración (Enum) que define los posibles estados de una
 * transacción distribuida bajo el patrón SAGA en la plataforma.
 *
 * ¿QUÉ ES UN 'ENUM' EN JAVA?
 * Un 'enum' es una lista fija de constantes que no pueden cambiar.
 * En lugar de usar strings como "PENDIENTE" o "COMPLETADO" donde un error
 * tipográfico provocaría bugs, Java te obliga a usar uno de estos valores.
 *
 * ¿QUÉ ES EL PATRÓN SAGA?
 * En microservicios no podemos hacer un 'BEGIN TRANSACTION ... COMMIT' que abarque
 * varias bases de datos diferentes. El patrón SAGA divide una transacción de negocio
 * larga en varios pasos locales coordinados. Si un paso falla, se ejecutan "acciones
 * de compensación" (deshacer los cambios previos).
 */
public enum SagaStatus {
    // 1. La transacción de cambio de divisa acaba de iniciarse
    SAGA_STARTED,

    // 2. El dinero de origen (ej. USD) fue retenido/bloqueado preventivamente en la billetera
    FUNDS_LOCKED,

    // 3. La tasa de cambio prometida al cliente fue verificada y sigue vigente
    RATE_VALIDATED,

    // 4. La transferencia o pago bancario fue confirmado satisfactoriamente
    PAYMENT_CONFIRMED,

    // 5. Los fondos de destino (ej. PEN) fueron depositados y el balance asentado en partida doble
    SETTLED,

    // 6. Ocurrió un error en algún paso intermedio y se ordenó cancelar y revertir
    COMPENSATION_REQUESTED,

    // 7. La compensación terminó con éxito: el dinero retenido se devolvió a la billetera
    COMPENSATED,

    // 8. Estado final de error si no se pudo completar la operación
    FAILED
}

