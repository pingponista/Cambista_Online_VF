package com.cambistaonline.auth.domain.valueobjects;

import com.cambistaonline.auth.domain.exceptions.DomainValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Password {
    // Requisitos: >= 8 caracteres, al menos 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._#-])[A-Za-z\\d@$!%*?&._#-]{8,}$"
    );

    private final String value;

    public Password(String value) {
        if (value == null || value.isEmpty()) {
            throw new DomainValidationException("La contraseña es obligatoria.");
        }
        if (!PASSWORD_PATTERN.matcher(value).matches()) {
            throw new DomainValidationException("La contraseña debe tener al menos 8 caracteres, incluir mayúscula, minúscula, número y un carácter especial.");
        }
        this.value = value;
    }

    public static Password fromHash(String hashValue) {
        if (hashValue == null || hashValue.isEmpty()) {
            throw new DomainValidationException("El hash de la contraseña es inválido.");
        }
        Password pass = new Password();
        pass.setHash(hashValue);
        return pass;
    }

    public static Password oauthDummy() {
        Password pass = new Password();
        pass.setHash("OAUTH_EXTERNAL_PROVIDER");
        return pass;
    }

    private Password() {
        this.value = null;
    }

    private String hash;
    private void setHash(String hash) { this.hash = hash; }

    public String getValue() {
        return hash != null ? hash : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(getValue(), password.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
