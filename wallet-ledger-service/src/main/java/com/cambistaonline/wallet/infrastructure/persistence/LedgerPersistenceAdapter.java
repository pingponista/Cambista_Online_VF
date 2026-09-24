package com.cambistaonline.wallet.infrastructure.persistence;

import com.cambistaonline.wallet.application.ports.outbound.LedgerEntryPersistencePort;
import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.EntryDirection;
import com.cambistaonline.wallet.domain.model.LedgerEntry;
import com.cambistaonline.wallet.domain.model.LedgerMovementType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LedgerPersistenceAdapter implements LedgerEntryPersistencePort {

    private final SpringDataJpaLedgerEntryRepository repository;

    public LedgerPersistenceAdapter(SpringDataJpaLedgerEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public LedgerEntry save(LedgerEntry entry) {
        LedgerEntryJpaEntity entity = toEntity(entry);
        LedgerEntryJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<LedgerEntry> findByUserEmail(String userEmail) {
        return repository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LedgerEntry> findByReferenceId(String referenceId) {
        return repository.findByReferenceId(referenceId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private LedgerEntryJpaEntity toEntity(LedgerEntry domain) {
        return new LedgerEntryJpaEntity(
                domain.getId(),
                domain.getUserEmail(),
                domain.getCurrency().name(),
                domain.getDirection().name(),
                domain.getAmount(),
                domain.getMovementType().name(),
                domain.getReferenceId(),
                domain.getDescription(),
                domain.getCreatedAt()
        );
    }

    private LedgerEntry toDomain(LedgerEntryJpaEntity entity) {
        return new LedgerEntry(
                entity.getId(),
                entity.getUserEmail(),
                AccountCurrency.valueOf(entity.getCurrency()),
                EntryDirection.valueOf(entity.getDirection()),
                entity.getAmount(),
                LedgerMovementType.valueOf(entity.getMovementType()),
                entity.getReferenceId(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}
