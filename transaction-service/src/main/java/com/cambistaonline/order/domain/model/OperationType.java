package com.cambistaonline.order.domain.model;

public enum OperationType {
    COMPRA,
    VENTA;

    public static OperationType from(String value) {
        if (value == null) return COMPRA;
        String val = value.trim().toUpperCase();
        if (val.contains("VENTA") || val.equals("V")) {
            return VENTA;
        }
        return COMPRA;
    }
}
