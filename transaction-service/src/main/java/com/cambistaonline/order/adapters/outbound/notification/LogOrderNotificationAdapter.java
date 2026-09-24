package com.cambistaonline.order.adapters.outbound.notification;

import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.domain.events.OrderCompletedEvent;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogOrderNotificationAdapter implements OrderNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogOrderNotificationAdapter.class);

    @Override
    public void notifyOrderCreated(OrderCreatedEvent event) {
        log.info("[NOTIFICATION SERVICE] 📨 Notificación enviada: Orden creada #{} para {}", event.getOrderNumber(), event.getUserEmail());
    }

    @Override
    public void notifyOrderCompleted(OrderCompletedEvent event) {
        log.info("[NOTIFICATION SERVICE] 📨 Notificación enviada: Orden completada #{} para {}", event.getOrderNumber(), event.getUserEmail());
    }
}
