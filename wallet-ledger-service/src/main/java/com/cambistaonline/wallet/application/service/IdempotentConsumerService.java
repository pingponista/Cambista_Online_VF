package com.cambistaonline.wallet.application.service;

import com.cambistaonline.wallet.infrastructure.persistence.ProcessedEventJpaEntity;
import com.cambistaonline.wallet.infrastructure.persistence.SpringDataJpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class IdempotentConsumerService {

    private static final Logger log = LoggerFactory.getLogger(IdempotentConsumerService.class);

    private final SpringDataJpaProcessedEventRepository processedEventRepository;

    public IdempotentConsumerService(SpringDataJpaProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional(readOnly = true)
    public boolean isEventProcessed(String eventId, String consumerGroup) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return processedEventRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup);
    }

    @Transactional
    public void markEventAsProcessed(String eventId, String aggregateId, String eventType, String consumerGroup) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        ProcessedEventJpaEntity entity = new ProcessedEventJpaEntity(
                eventId,
                aggregateId != null ? aggregateId : "UNKNOWN",
                eventType != null ? eventType : "GENERIC_EVENT",
                consumerGroup != null ? consumerGroup : "DEFAULT_GROUP",
                LocalDateTime.now()
        );
        processedEventRepository.save(entity);
        log.debug("[IDEMPOTENCY] Evento marcado como procesado: {} (Grupo: {})", eventId, consumerGroup);
    }
}
