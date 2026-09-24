package com.cambistaonline.wallet.application.dto;

import com.cambistaonline.wallet.domain.model.AccountCurrency;
import com.cambistaonline.wallet.domain.model.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceDto(
        UUID accountId,
        String userEmail,
        AccountCurrency currency,
        BigDecimal availableBalance,
        BigDecimal lockedBalance,
        BigDecimal totalBalance,
        AccountStatus status
) {}
