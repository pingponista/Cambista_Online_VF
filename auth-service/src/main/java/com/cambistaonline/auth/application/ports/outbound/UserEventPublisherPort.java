package com.cambistaonline.auth.application.ports.outbound;

import com.cambistaonline.auth.domain.events.UserRegisteredEvent;

/**
 * Puerto de salida (Outbound Port) para la publicación de eventos de usuario.
 * La implementación (Kafka) vive en la capa de adaptadores.
 */
public interface UserEventPublisherPort {
    void publishUserRegistered(UserRegisteredEvent event);
}
