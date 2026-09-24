package com.cambistaonline.auth.domain.model;

/**
 * Proveedores de autenticación soportados en CambistaOnline.
 * Pertenece al núcleo de dominio: sin dependencias de frameworks externos.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB,
    FACEBOOK;

    public static AuthProvider fromString(String provider) {
        if (provider == null) {
            return LOCAL;
        }
        for (AuthProvider p : values()) {
            if (p.name().equalsIgnoreCase(provider.trim())) {
                return p;
            }
        }
        return LOCAL;
    }
}
