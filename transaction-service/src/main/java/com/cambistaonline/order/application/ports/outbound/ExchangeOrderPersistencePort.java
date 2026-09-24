package com.cambistaonline.order.application.ports.outbound;

import com.cambistaonline.order.domain.model.ExchangeOrder;
import java.util.List;
import java.util.Optional;

public interface ExchangeOrderPersistencePort {
    ExchangeOrder save(ExchangeOrder order);
    Optional<ExchangeOrder> findByOrderNumber(String orderNumber);
    List<ExchangeOrder> findByUserEmail(String userEmail);
}
