package com.cambistaonline.common.saga;

import java.time.LocalDateTime;
import java.util.UUID;

public class SagaStepEvent {

    private String eventId;
    private String sagaId;
    private String stepName;
    private SagaStatus status;
    private String details;
    private LocalDateTime timestamp;

    public SagaStepEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

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
