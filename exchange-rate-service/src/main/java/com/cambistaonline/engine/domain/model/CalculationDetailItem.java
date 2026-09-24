package com.cambistaonline.engine.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationDetailItem {
    private final String concepto;
    private final BigDecimal valor;

    public CalculationDetailItem(String concepto, BigDecimal valor) {
        this.concepto = concepto;
        this.valor = valor != null ? valor.setScale(4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
    }

    public String getConcepto() {
        return concepto;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
