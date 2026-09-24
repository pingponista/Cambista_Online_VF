package com.cambistaonline.wallet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaProcessedEventRepository extends JpaRepository<ProcessedEventJpaEntity, String> {
    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
