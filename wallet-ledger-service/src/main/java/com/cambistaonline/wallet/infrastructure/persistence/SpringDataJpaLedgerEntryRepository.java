package com.cambistaonline.wallet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaLedgerEntryRepository extends JpaRepository<LedgerEntryJpaEntity, Long> {
    List<LedgerEntryJpaEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<LedgerEntryJpaEntity> findByReferenceId(String referenceId);
}
