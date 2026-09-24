package com.cambistaonline.wallet.domain.model;

import com.cambistaonline.wallet.domain.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletAccount {

    private final UUID id;
    private final String userEmail;
    private final AccountCurrency currency;
    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;
    private AccountStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WalletAccount(UUID id, String userEmail, AccountCurrency currency, BigDecimal availableBalance,
                         BigDecimal lockedBalance, AccountStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.userEmail = userEmail;
        this.currency = currency;
        this.availableBalance = availableBalance != null ? availableBalance : BigDecimal.ZERO;
        this.lockedBalance = lockedBalance != null ? lockedBalance : BigDecimal.ZERO;
        this.status = status != null ? status : AccountStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static WalletAccount createInitial(String userEmail, AccountCurrency currency) {
        return new WalletAccount(
                UUID.randomUUID(),
                userEmail,
                currency,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser mayor que cero.");
        }
        this.availableBalance = this.availableBalance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a debitar debe ser mayor que cero.");
        }
        if (this.availableBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    String.format("Fondos insuficientes en %s. Saldo disponible: %s, Solicitado: %s",
                            currency, availableBalance, amount)
            );
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void lockFunds(BigDecimal amount) {
        if (this.availableBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Fondos insuficientes para reservar.");
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.lockedBalance = this.lockedBalance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void releaseFunds(BigDecimal amount) {
        if (this.lockedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("El monto a liberar excede los fondos reservados.");
        }
        this.lockedBalance = this.lockedBalance.subtract(amount);
        this.availableBalance = this.availableBalance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public AccountCurrency getCurrency() { return currency; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public BigDecimal getTotalBalance() { return availableBalance.add(lockedBalance); }
    public AccountStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
