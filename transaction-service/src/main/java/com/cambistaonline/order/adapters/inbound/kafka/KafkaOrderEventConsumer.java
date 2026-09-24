package com.cambistaonline.order.adapters.inbound.kafka;

import com.cambistaonline.order.domain.events.OrderCompletedEvent;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada (Inbound Adapter) que escucha eventos de orden en Kafka.
 * Simula el servicio de auditoría: registra en el log cada evento recibido.
 */
@Component
public class KafkaOrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventConsumer.class);

    /**
     * Escucha el tópico cambista.orders.created.
     */
    @KafkaListener(
        topics = "cambista.orders.created",
        groupId = "cambista-group",
        containerFactory = "orderCreatedContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("════════════════════════════════════════════════════");
        log.info("[KAFKA AUDIT] ✅ ORDEN CREADA");
        log.info("[KAFKA AUDIT]    Número    : {}", event.getOrderNumber());
        log.info("[KAFKA AUDIT]    Usuario   : {}", event.getUserEmail());
        log.info("[KAFKA AUDIT]    Operación : {}", event.getOperationType());
        log.info("[KAFKA AUDIT]    {} → {}", event.getCurrencyOrigin(), event.getCurrencyDestination());
        log.info("[KAFKA AUDIT]    Monto env : {} | Monto rec: {}", event.getAmountSent(), event.getAmountReceived());
        log.info("[KAFKA AUDIT]    Tasa      : {}", event.getExchangeRate());
        log.info("[KAFKA AUDIT]    Timestamp : {}", event.getOccurredAt());
        log.info("════════════════════════════════════════════════════");
    }

    /**
     * Escucha el tópico cambista.orders.completed.
     */
    @KafkaListener(
        topics = "cambista.orders.completed",
        groupId = "cambista-group",
        containerFactory = "orderCompletedContainerFactory"
    )
    public void onOrderCompleted(OrderCompletedEvent event) {
        log.info("════════════════════════════════════════════════════");
        log.info("[KAFKA AUDIT] ✅ ORDEN COMPLETADA");
        log.info("[KAFKA AUDIT]    Número    : {}", event.getOrderNumber());
        log.info("[KAFKA AUDIT]    Usuario   : {}", event.getUserEmail());
        log.info("[KAFKA AUDIT]    {} → {}", event.getCurrencyOrigin(), event.getCurrencyDestination());
        log.info("[KAFKA AUDIT]    Monto env : {} | Monto rec: {}", event.getAmountSent(), event.getAmountReceived());
        log.info("[KAFKA AUDIT]    Timestamp : {}", event.getOccurredAt());
        log.info("════════════════════════════════════════════════════");
    }
}
