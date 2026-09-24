package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_tasa_base")
public class BaseRateJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "moneda_origen", nullable = false, length = 10)
    private String monedaOrigen;

    @Column(name = "moneda_destino", nullable = false, length = 10)
    private String monedaDestino;

    @Column(name = "valor_compra", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorCompra;

    @Column(name = "valor_venta", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorVenta;

    @Column(name = "fecha_efectiva", nullable = false)
    private LocalDateTime fechaEfectiva;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public BaseRateJpaEntity() {}

    public Long getId() { return id; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public String getMonedaDestino() { return monedaDestino; }
    public BigDecimal getValorCompra() { return valorCompra; }
    public BigDecimal getValorVenta() { return valorVenta; }
    public LocalDateTime getFechaEfectiva() { return fechaEfectiva; }
    public boolean isActive() { return active; }
}
