package com.cambistaonline.engine.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_spread_nivel")
public class CustomerSpreadJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_usuario", nullable = false, length = 5)
    private String tipoUsuario;

    @Column(name = "nivel_cliente", nullable = false, length = 20)
    private String nivelCliente;

    @Column(name = "spread_compra", nullable = false, precision = 10, scale = 4)
    private BigDecimal spreadCompra;

    @Column(name = "spread_venta", nullable = false, precision = 10, scale = 4)
    private BigDecimal spreadVenta;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public CustomerSpreadJpaEntity() {}

    public Long getId() { return id; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getNivelCliente() { return nivelCliente; }
    public BigDecimal getSpreadCompra() { return spreadCompra; }
    public BigDecimal getSpreadVenta() { return spreadVenta; }
    public boolean isActive() { return active; }
}
