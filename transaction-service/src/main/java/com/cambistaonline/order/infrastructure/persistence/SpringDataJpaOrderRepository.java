package com.cambistaonline.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpringDataJpaOrderRepository extends JpaRepository<OrderJpaEntity, Long> {
    Optional<OrderJpaEntity> findByOrderNumber(String orderNumber);
    List<OrderJpaEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
