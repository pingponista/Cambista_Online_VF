package com.cambistaonline.engine.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OperationType {
    COMPRA("COMPRA"),
    VENTA("VENTA");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OperationType from(String value) {
        if (value == null) return COMPRA;
        String val = value.trim().toUpperCase();
        if (val.equals("BUY") || val.equals("COMPRA")) {
            return COMPRA;
        }
        if (val.equals("SELL") || val.equals("VENTA")) {
            return VENTA;
        }
        return COMPRA;
    }
}
