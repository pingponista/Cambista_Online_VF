package com.cambistaonline.order.application.ports.outbound;

import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.events.OrderCompletedEvent;

/**
 * Puerto de salida (Outbound Port) para la publicación de eventos de orden.
 * El dominio y los casos de uso dependen de esta interfaz.
 * La implementación (Kafka) vive en la capa de adaptadores.
 */
public interface OrderEventPublisherPort {
    void publishOrderCreated(OrderCreatedEvent event);
    void publishOrderCompleted(OrderCompletedEvent event);
}
