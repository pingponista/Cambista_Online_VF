package com.cambistaonline.order.application.ports.outbound;

import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.events.OrderCompletedEvent;

/**
 * Puerto de salida (Outbound Port) para el envío de notificaciones de orden.
 * El dominio y los casos de uso dependen de esta interfaz.
 * La implementación (RabbitMQ) vive en la capa de adaptadores.
 */
public interface OrderNotificationPort {
    void notifyOrderCreated(OrderCreatedEvent event);
    void notifyOrderCompleted(OrderCompletedEvent event);
}
