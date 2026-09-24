package com.cambistaonline.wallet.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_wallet_account", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_email", "currency"})
})
public class WalletAccountJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_email", nullable = false, length = 150)
    private String userEmail;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "available_balance", nullable = false, precision = 14, scale = 4)
    private BigDecimal availableBalance;

    @Column(name = "locked_balance", nullable = false, precision = 14, scale = 4)
    private BigDecimal lockedBalance;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WalletAccountJpaEntity() {}

    public WalletAccountJpaEntity(UUID id, String userEmail, String currency, BigDecimal availableBalance,
                                  BigDecimal lockedBalance, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.currency = currency;
        this.availableBalance = availableBalance;
        this.lockedBalance = lockedBalance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(BigDecimal lockedBalance) { this.lockedBalance = lockedBalance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
