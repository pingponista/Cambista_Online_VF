package com.cambistaonline.auth.domain.valueobjects;

import com.cambistaonline.auth.domain.exceptions.DomainValidationException;
import java.util.Objects;

public class Ruc {
    private final String value;

    public Ruc(String value) {
        if (value != null && !value.trim().isEmpty()) {
            String trimmed = value.trim();
            if (!trimmed.matches("^(10|20)\\d{9}$")) {
                throw new DomainValidationException("El RUC debe tener 11 dígitos numéricos y comenzar con 10 o 20.");
            }
            this.value = trimmed;
        } else {
            this.value = null;
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruc ruc = (Ruc) o;
        return Objects.equals(value, ruc.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
