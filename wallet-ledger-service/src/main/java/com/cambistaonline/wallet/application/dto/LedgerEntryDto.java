package com.cambistaonline.wallet.application.dto;

import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.EntryDirection;
import com.cambistaonline.wallet.domain.model.LedgerMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LedgerEntryDto(
        Long id,
        String userEmail,
        AccountCurrency currency,
        EntryDirection direction,
        BigDecimal amount,
        LedgerMovementType movementType,
        String referenceId,
        String description,
        LocalDateTime createdAt
) {}
