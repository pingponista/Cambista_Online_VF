package com.cambistaonline.wallet.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_loyalty_points")
public class LoyaltyPointsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false, unique = true, length = 150)
    private String userEmail;

    @Column(name = "saldo_puntos", nullable = false)
    private int saldoPuntos;

    @Column(name = "puntos_acumulados", nullable = false)
    private int puntosAcumulados;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LoyaltyPointsJpaEntity() {}

    public LoyaltyPointsJpaEntity(Long id, String userEmail, int saldoPuntos, int puntosAcumulados,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.saldoPuntos = saldoPuntos;
        this.puntosAcumulados = puntosAcumulados;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public int getSaldoPuntos() { return saldoPuntos; }
    public void setSaldoPuntos(int saldoPuntos) { this.saldoPuntos = saldoPuntos; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
