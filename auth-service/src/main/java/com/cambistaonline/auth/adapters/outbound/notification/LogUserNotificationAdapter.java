package com.cambistaonline.auth.adapters.outbound.notification;

import com.cambistaonline.auth.application.ports.outbound.UserNotificationPort;
import com.cambistaonline.auth.domain.events.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogUserNotificationAdapter implements UserNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogUserNotificationAdapter.class);

    @Override
    public void notifyWelcome(UserRegisteredEvent event) {
        log.info("[NOTIFICATION SERVICE] 📨 Notificación de bienvenida generada exitosamente para: {}", event.getEmail());
    }
}
