package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_puntos_config")
public class PointsConfigJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_usuario", nullable = false, length = 5, unique = true)
    private String tipoUsuario;

    @Column(name = "canje_minimo", nullable = false)
    private int canjeMinimo;

    @Column(name = "puntos_por_bloque", nullable = false)
    private int puntosPorBloque;

    @Column(name = "mejora_por_bloque", nullable = false, precision = 10, scale = 4)
    private BigDecimal mejoraPorBloque;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public PointsConfigJpaEntity() {}

    public Long getId() { return id; }
    public String getTipoUsuario() { return tipoUsuario; }
    public int getCanjeMinimo() { return canjeMinimo; }
    public int getPuntosPorBloque() { return puntosPorBloque; }
    public BigDecimal getMejoraPorBloque() { return mejoraPorBloque; }
    public boolean isActive() { return active; }
}
