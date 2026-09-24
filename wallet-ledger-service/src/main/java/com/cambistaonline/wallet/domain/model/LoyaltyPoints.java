package com.cambistaonline.wallet.domain.model;

import java.time.LocalDateTime;

public class LoyaltyPoints {

    private final Long id;
    private final String userEmail;
    private int saldoPuntos;
    private int puntosAcumulados;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LoyaltyPoints(Long id, String userEmail, int saldoPuntos, int puntosAcumulados,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.saldoPuntos = saldoPuntos;
        this.puntosAcumulados = puntosAcumulados;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static LoyaltyPoints createInitial(String userEmail, int initialPoints) {
        return new LoyaltyPoints(null, userEmail, initialPoints, initialPoints, LocalDateTime.now(), LocalDateTime.now());
    }

    public void awardPoints(int points) {
        if (points <= 0) return;
        this.saldoPuntos += points;
        this.puntosAcumulados += points;
        this.updatedAt = LocalDateTime.now();
    }

    public void redeemPoints(int points) {
        if (points <= 0) return;
        if (this.saldoPuntos < points) {
            throw new IllegalArgumentException("Saldo insuficiente de puntos. Disponible: " + saldoPuntos + ", Solicitado: " + points);
        }
        this.saldoPuntos -= points;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public int getSaldoPuntos() { return saldoPuntos; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
