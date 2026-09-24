package com.cambistaonline.order.adapters.outbound.kafka;

import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.domain.events.OrderCompletedEvent;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida (Outbound Adapter) que implementa OrderEventPublisherPort.
 * Es el único lugar del proyecto que conoce Apache Kafka.
 * Publica eventos de orden en los tópicos correspondientes.
 */
@Component
public class KafkaOrderEventPublisherAdapter implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisherAdapter.class);

    static final String TOPIC_ORDER_CREATED   = "cambista.orders.created";
    static final String TOPIC_ORDER_COMPLETED = "cambista.orders.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[KAFKA] Publicando OrderCreatedEvent → tópico={} | orden={} | usuario={} | correlationId={}",
                TOPIC_ORDER_CREATED, event.getOrderNumber(), event.getUserEmail(), com.cambistaonline.common.tracing.CorrelationContext.getCorrelationId());

        org.apache.kafka.clients.producer.ProducerRecord<String, Object> record =
                new org.apache.kafka.clients.producer.ProducerRecord<>(TOPIC_ORDER_CREATED, event.getOrderNumber(), event);

        addTracingAndSecurityHeaders(record, event.getUserEmail());
        kafkaTemplate.send(record);
    }

    @Override
    public void publishOrderCompleted(OrderCompletedEvent event) {
        log.info("[KAFKA] Publicando OrderCompletedEvent → tópico={} | orden={} | usuario={} | correlationId={}",
                TOPIC_ORDER_COMPLETED, event.getOrderNumber(), event.getUserEmail(), com.cambistaonline.common.tracing.CorrelationContext.getCorrelationId());

        org.apache.kafka.clients.producer.ProducerRecord<String, Object> record =
                new org.apache.kafka.clients.producer.ProducerRecord<>(TOPIC_ORDER_COMPLETED, event.getOrderNumber(), event);

        addTracingAndSecurityHeaders(record, event.getUserEmail());
        kafkaTemplate.send(record);
    }

    private void addTracingAndSecurityHeaders(org.apache.kafka.clients.producer.ProducerRecord<String, Object> record, String userEmail) {
        String correlationId = com.cambistaonline.common.tracing.CorrelationContext.getCorrelationId();
        boolean mfaVerified = com.cambistaonline.common.tracing.CorrelationContext.isMfaVerified();

        record.headers().add(com.cambistaonline.common.security.SecurityContextMetadata.KAFKA_HEADER_CORRELATION_ID,
                correlationId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (userEmail != null) {
            record.headers().add(com.cambistaonline.common.security.SecurityContextMetadata.KAFKA_HEADER_USER_EMAIL,
                    userEmail.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        record.headers().add(com.cambistaonline.common.security.SecurityContextMetadata.KAFKA_HEADER_MFA_VERIFIED,
                String.valueOf(mfaVerified).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
