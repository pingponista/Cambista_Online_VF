package com.cambistaonline.auth.application.ports.outbound;

import com.cambistaonline.auth.domain.events.UserRegisteredEvent;

/**
 * Puerto de salida (Outbound Port) para el envío de notificaciones de usuario.
 * La implementación (RabbitMQ) vive en la capa de adaptadores.
 */
public interface UserNotificationPort {
    void notifyWelcome(UserRegisteredEvent event);
}
