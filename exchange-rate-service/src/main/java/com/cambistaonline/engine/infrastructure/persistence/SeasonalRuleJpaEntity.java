package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_estacionalidad")
public class SeasonalRuleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_usuario", nullable = false, length = 5)
    private String tipoUsuario;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "ajuste_compra", nullable = false, precision = 10, scale = 4)
    private BigDecimal ajusteCompra;

    @Column(name = "ajuste_venta", nullable = false, precision = 10, scale = 4)
    private BigDecimal ajusteVenta;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public SeasonalRuleJpaEntity() {}

    public Long getId() { return id; }
    public String getTipoUsuario() { return tipoUsuario; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public BigDecimal getAjusteCompra() { return ajusteCompra; }
    public BigDecimal getAjusteVenta() { return ajusteVenta; }
    public boolean isActive() { return active; }
}
