package com.cambistaonline.auth.domain.model;

public enum UserRole {
    N("Persona Natural"),
    J("Persona Jurídica"),
    ADMIN("Administrador");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromString(String role) {
        if (role == null) return N;
        for (UserRole r : UserRole.values()) {
            if (r.name().equalsIgnoreCase(role.trim())) {
                return r;
            }
        }
        return N;
    }
}
