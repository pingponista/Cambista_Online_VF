package com.cambistaonline.order.infrastructure.persistence;

import com.cambistaonline.order.application.ports.outbound.ExchangeOrderPersistencePort;
import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.model.OrderStatus;
import com.cambistaonline.order.domain.model.OperationType;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExchangeOrderPersistenceAdapter implements ExchangeOrderRepositoryPort, ExchangeOrderPersistencePort {

    private final SpringDataJpaOrderRepository orderRepository;

    public ExchangeOrderPersistenceAdapter(SpringDataJpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public ExchangeOrder save(ExchangeOrder order) {
        OrderJpaEntity entity = mapToJpaEntity(order);
        OrderJpaEntity saved = orderRepository.save(entity);
        return mapToDomainEntity(saved);
    }

    @Override
    public Optional<ExchangeOrder> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).map(this::mapToDomainEntity);
    }

    @Override
    public List<ExchangeOrder> findByUserEmail(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private OrderJpaEntity mapToJpaEntity(ExchangeOrder domain) {
        OrderJpaEntity entity = new OrderJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setOrderNumber(domain.getOrderNumber());
        entity.setOperationType(domain.getOperationType().name());
        entity.setCurrencyOrigin(domain.getCurrencyOrigin().name());
        entity.setCurrencyDestination(domain.getCurrencyDestination().name());
        entity.setAmountSent(domain.getAmountSent());
        entity.setAmountReceived(domain.getAmountReceived());
        entity.setExchangeRate(domain.getExchangeRate());
        entity.setPointsRedeemed(domain.getPointsRedeemed());
        entity.setStatus(domain.getStatus().name());
        entity.setUserEmail(domain.getUserEmail());
        entity.setUserRole(domain.getUserRole());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private ExchangeOrder mapToDomainEntity(OrderJpaEntity jpa) {
        return new ExchangeOrder(
                jpa.getId(),
                jpa.getOrderNumber(),
                OperationType.valueOf(jpa.getOperationType()),
                CurrencyType.valueOf(jpa.getCurrencyOrigin()),
                CurrencyType.valueOf(jpa.getCurrencyDestination()),
                jpa.getAmountSent(),
                jpa.getAmountReceived(),
                jpa.getExchangeRate(),
                jpa.getPointsRedeemed(),
                OrderStatus.valueOf(jpa.getStatus()),
                jpa.getUserEmail(),
                jpa.getUserRole(),
                jpa.getExpiresAt(),
                jpa.getCreatedAt()
        );
    }
}
