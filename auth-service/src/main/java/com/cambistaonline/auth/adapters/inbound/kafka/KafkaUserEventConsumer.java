package com.cambistaonline.auth.adapters.inbound.kafka;

import com.cambistaonline.auth.domain.events.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada (Inbound Adapter) que escucha eventos de usuario en Kafka.
 * Registra en el log cada nuevo registro de usuario para auditoría.
 */
@Component
public class KafkaUserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserEventConsumer.class);

    @KafkaListener(
        topics = "cambista.users.registered",
        groupId = "cambista-group",
        containerFactory = "userRegisteredContainerFactory"
    )
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("════════════════════════════════════════════════════");
        log.info("[KAFKA AUDIT] 👤 NUEVO USUARIO REGISTRADO");
        log.info("[KAFKA AUDIT]    ID        : {}", event.getUserId());
        log.info("[KAFKA AUDIT]    Email     : {}", event.getEmail());
        log.info("[KAFKA AUDIT]    Nombre    : {} {}", event.getFirstName(), event.getLastName());
        log.info("[KAFKA AUDIT]    Rol       : {}", event.getRole());
        log.info("[KAFKA AUDIT]    Timestamp : {}", event.getOccurredAt());
        log.info("════════════════════════════════════════════════════");
    }
}
