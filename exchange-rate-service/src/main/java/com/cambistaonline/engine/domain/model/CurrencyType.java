package com.cambistaonline.engine.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyType {
    USD("USD"),
    PEN("PEN"),
    EUR("EUR");

    private final String code;

    CurrencyType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static CurrencyType from(String value) {
        if (value == null) return USD;
        String val = value.trim().toUpperCase();
        for (CurrencyType type : values()) {
            if (type.name().equals(val) || type.code.equals(val)) {
                return type;
            }
        }
        return USD;
    }
}
