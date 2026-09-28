package com.cambistaonline.common.saga;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SagaStepEvent: Representa un evento atómico emitido cada vez que un paso del SAGA
 * cambia de estado (ej: "Se bloquearon los fondos", "Falló la tasa de cambio").
 *
 * CONCEPTOS JAVA CLAVE PARA PRINCIPIANTES:
 * 1. POJO (Plain Old Java Object): Una clase con atributos privados, constructores y métodos getter/setter.
 * 2. 'private': Encapsulación. Ninguna otra clase puede modificar estas variables directamente.
 * 3. Constructor por defecto (sin argumentos): Requerido por librerías como Jackson/Kafka para
 *    reconstruir el objeto a partir de un JSON (deserialización).
 * 4. 'UUID.randomUUID()': Genera un identificador único global (36 caracteres) para no repetir eventos.
 */
public class SagaStepEvent {

    // Identificador único del evento generado automáticamente
    private String eventId;

    // Identificador de la saga (corresponde al número de orden o traceId)
    private String sagaId;

    // Nombre legible del paso que se ejecutó (ej. "LOCK_FUNDS", "VERIFY_PAYMENT")
    private String stepName;

    // Estado del paso (usa el enum SagaStatus)
    private SagaStatus status;

    // Mensaje explicativo o detalles técnicos adicionales (ej. motivo de error)
    private String details;

    // Momento exacto en que ocurrió el evento en el sistema
    private LocalDateTime timestamp;

    // Constructor sin argumentos: Necesario para que Kafka/Jackson convierta de JSON a Java
    public SagaStepEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    // Constructor con parámetros: Usado por el código para crear un evento fácilmente con datos
    public SagaStepEvent(String sagaId, String stepName, SagaStatus status, String details) {
        this.eventId = UUID.randomUUID().toString();
        this.sagaId = sagaId;
        this.stepName = stepName;
        this.status = status;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }


    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }
    public SagaStatus getStatus() { return status; }
    public void setStatus(SagaStatus status) { this.status = status; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
