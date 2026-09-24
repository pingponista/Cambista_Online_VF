package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "tb_horario")
public class HourlyRuleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_usuario", nullable = false, length = 5)
    private String tipoUsuario;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "ajuste_compra", nullable = false, precision = 10, scale = 4)
    private BigDecimal ajusteCompra;

    @Column(name = "ajuste_venta", nullable = false, precision = 10, scale = 4)
    private BigDecimal ajusteVenta;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public HourlyRuleJpaEntity() {}

    public Long getId() { return id; }
    public String getTipoUsuario() { return tipoUsuario; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public BigDecimal getAjusteCompra() { return ajusteCompra; }
    public BigDecimal getAjusteVenta() { return ajusteVenta; }
    public boolean isActive() { return active; }
}
