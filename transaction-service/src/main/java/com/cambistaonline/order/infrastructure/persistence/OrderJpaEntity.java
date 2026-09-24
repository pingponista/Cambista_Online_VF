package com.cambistaonline.order.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_operacion")
public class OrderJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nro_orden", nullable = false, length = 50, unique = true)
    private String orderNumber;

    @Column(name = "tipo_operacion", nullable = false, length = 20)
    private String operationType;

    @Column(name = "moneda_origen", nullable = false, length = 10)
    private String currencyOrigin;

    @Column(name = "moneda_destino", nullable = false, length = 10)
    private String currencyDestination;

    @Column(name = "monto_origen", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountSent;

    @Column(name = "monto_destino", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountReceived;

    @Column(name = "tasa_final", nullable = false, precision = 10, scale = 4)
    private BigDecimal exchangeRate;

    @Column(name = "puntos_canjeados", nullable = false)
    private int pointsRedeemed;

    @Column(name = "estado", nullable = false, length = 30)
    private String status;

    @Column(name = "correo_user", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "rol", nullable = false, length = 5)
    private String userRole;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderJpaEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getCurrencyOrigin() { return currencyOrigin; }
    public void setCurrencyOrigin(String currencyOrigin) { this.currencyOrigin = currencyOrigin; }

    public String getCurrencyDestination() { return currencyDestination; }
    public void setCurrencyDestination(String currencyDestination) { this.currencyDestination = currencyDestination; }

    public BigDecimal getAmountSent() { return amountSent; }
    public void setAmountSent(BigDecimal amountSent) { this.amountSent = amountSent; }

    public BigDecimal getAmountReceived() { return amountReceived; }
    public void setAmountReceived(BigDecimal amountReceived) { this.amountReceived = amountReceived; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public int getPointsRedeemed() { return pointsRedeemed; }
    public void setPointsRedeemed(int pointsRedeemed) { this.pointsRedeemed = pointsRedeemed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
