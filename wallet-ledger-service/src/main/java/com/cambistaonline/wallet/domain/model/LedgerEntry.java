package com.cambistaonline.wallet.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LedgerEntry {

    private final Long id;
    private final String userEmail;
    private final AccountCurrency currency;
    private final EntryDirection direction;
    private final BigDecimal amount;
    private final LedgerMovementType movementType;
    private final String referenceId;
    private final String description;
    private final LocalDateTime createdAt;

    public LedgerEntry(Long id, String userEmail, AccountCurrency currency, EntryDirection direction,
                       BigDecimal amount, LedgerMovementType movementType, String referenceId,
                       String description, LocalDateTime createdAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.currency = currency;
        this.direction = direction;
        this.amount = amount;
        this.movementType = movementType;
        this.referenceId = referenceId;
        this.description = description;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static LedgerEntry create(String userEmail, AccountCurrency currency, EntryDirection direction,
                                     BigDecimal amount, LedgerMovementType movementType,
                                     String referenceId, String description) {
        return new LedgerEntry(null, userEmail, currency, direction, amount, movementType, referenceId, description, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public AccountCurrency getCurrency() { return currency; }
    public EntryDirection getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public LedgerMovementType getMovementType() { return movementType; }
    public String getReferenceId() { return referenceId; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
