package com.cambistaonline.common.saga;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OrderCompensatedEvent: Evento emitido cuando una orden de cambio es cancelada o compensada,
 * informando que los fondos que estaban retenidos fueron desbloqueados.
 *
 * ¿POR QUÉ USAMOS 'BigDecimal' EN LUGAR DE 'double' O 'float'?
 * En aplicaciones financieras (Fintech, banca, cambio de divisas), NUNCA se debe usar
 * 'double' o 'float' porque sufren de errores de redondeo de punto flotante binario
 * (ejemplo: en double, 0.1 + 0.2 da 0.30000000000000004).
 * 'BigDecimal' en Java ofrece precisión decimal exacta y control total del redondeo.
 */
public class OrderCompensatedEvent {

    // Identificador único del evento
    private String eventId;

    // Código de la orden (ej: ORD-2026-0001)
    private String orderNumber;

    // Email del usuario dueño de los fondos
    private String userEmail;

    // Moneda de origen que fue desbloqueada (ej. "USD", "PEN")
    private String currencyOrigin;

    // Monto exacto que fue devuelto/desbloqueado
    private BigDecimal amountUnlocked;

    // Motivo por el cual se compensó (ej: "Cotización expirada", "Cancelado por usuario")
    private String reason;

    // Fecha y hora en que se ejecutó la compensación
    private LocalDateTime occurredAt;

    public OrderCompensatedEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
    }

    public OrderCompensatedEvent(String orderNumber, String userEmail, String currencyOrigin,
                                 BigDecimal amountUnlocked, String reason) {
        this.eventId = UUID.randomUUID().toString();
        this.orderNumber = orderNumber;
        this.userEmail = userEmail;
        this.currencyOrigin = currencyOrigin;
        this.amountUnlocked = amountUnlocked;
        this.reason = reason;
        this.occurredAt = LocalDateTime.now();
    }


    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getCurrencyOrigin() { return currencyOrigin; }
    public void setCurrencyOrigin(String currencyOrigin) { this.currencyOrigin = currencyOrigin; }
    public BigDecimal getAmountUnlocked() { return amountUnlocked; }
    public void setAmountUnlocked(BigDecimal amountUnlocked) { this.amountUnlocked = amountUnlocked; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
