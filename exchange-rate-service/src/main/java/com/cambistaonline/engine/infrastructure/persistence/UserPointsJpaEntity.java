package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuario_puntos")
public class UserPointsJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false, length = 150, unique = true)
    private String userEmail;

    @Column(name = "saldo_puntos", nullable = false)
    private int saldoPuntos;

    @Column(name = "puntos_acumulados", nullable = false)
    private int puntosAcumulados;

    @Column(name = "created_by", nullable = false)
    private String createdBy = "SYSTEM";

    public UserPointsJpaEntity() {}

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getSaldoPuntos() { return saldoPuntos; }
    public void setSaldoPuntos(int saldoPuntos) { this.saldoPuntos = saldoPuntos; }

    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }
}
