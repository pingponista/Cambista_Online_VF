package com.cambistaonline.order.domain.ports;

import com.cambistaonline.order.domain.model.ExchangeOrder;

import java.util.List;
import java.util.Optional;

public interface ExchangeOrderRepositoryPort {
    ExchangeOrder save(ExchangeOrder order);
    Optional<ExchangeOrder> findByOrderNumber(String orderNumber);
    List<ExchangeOrder> findByUserEmail(String userEmail);
}
