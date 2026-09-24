package com.cambistaonline.auth.domain.valueobjects;

import com.cambistaonline.auth.domain.exceptions.DomainValidationException;
import java.util.Objects;

public class Dni {
    private final String value;

    public Dni(String value) {
        if (value != null && !value.trim().isEmpty()) {
            String trimmed = value.trim();
            if (!trimmed.matches("^\\d{8}$")) {
                throw new DomainValidationException("El DNI debe contener exactamente 8 dígitos numéricos.");
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
        Dni dni = (Dni) o;
        return Objects.equals(value, dni.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
