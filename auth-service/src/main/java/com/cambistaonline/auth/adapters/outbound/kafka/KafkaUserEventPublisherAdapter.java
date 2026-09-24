package com.cambistaonline.auth.adapters.outbound.kafka;

import com.cambistaonline.auth.application.ports.outbound.UserEventPublisherPort;
import com.cambistaonline.auth.domain.events.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida (Outbound Adapter) que implementa UserEventPublisherPort.
 * Publica eventos de usuario en los tópicos Kafka correspondientes.
 */
@Component
public class KafkaUserEventPublisherAdapter implements UserEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserEventPublisherAdapter.class);

    static final String TOPIC_USER_REGISTERED = "cambista.users.registered";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaUserEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("[KAFKA] Publicando UserRegisteredEvent → tópico={} | email={}",
                TOPIC_USER_REGISTERED, event.getEmail());
        kafkaTemplate.send(TOPIC_USER_REGISTERED, event.getUserId(), event);
    }
}
