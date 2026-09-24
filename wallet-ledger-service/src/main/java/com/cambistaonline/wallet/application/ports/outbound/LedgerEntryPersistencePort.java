package com.cambistaonline.wallet.application.ports.outbound;

import com.cambistaonline.wallet.domain.model.LedgerEntry;

import java.util.List;

public interface LedgerEntryPersistencePort {
    LedgerEntry save(LedgerEntry entry);
    List<LedgerEntry> findByUserEmail(String userEmail);
    List<LedgerEntry> findByReferenceId(String referenceId);
}
