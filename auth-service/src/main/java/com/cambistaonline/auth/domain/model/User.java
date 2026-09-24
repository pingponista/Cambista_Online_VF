package com.cambistaonline.auth.domain.model;

import com.cambistaonline.auth.domain.valueobjects.Dni;
import com.cambistaonline.auth.domain.valueobjects.Email;
import com.cambistaonline.auth.domain.valueobjects.Password;
import com.cambistaonline.auth.domain.valueobjects.Ruc;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final Email email;
    private Password password;
    private final String firstName;
    private final String lastName;
    private final Dni dni;
    private final String companyName;
    private final Ruc ruc;
    private final String legalRepresentativeName;
    private final String role; // "N" o "J" o "ADMIN"
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // MFA & OAuth
    private boolean mfaEnabled;
    private String mfaSecret;
    private AuthProvider authProvider;
    private String providerId;

    public User(UUID id, Email email, Password password, String firstName, String lastName, Dni dni, String companyName, Ruc ruc, String legalRepresentativeName, String role, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, email, password, firstName, lastName, dni, companyName, ruc, legalRepresentativeName, role, status, createdAt, updatedAt, false, null, AuthProvider.LOCAL, null);
    }

    public User(UUID id, Email email, Password password, String firstName, String lastName, Dni dni, String companyName, Ruc ruc, String legalRepresentativeName, String role, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, boolean mfaEnabled, String mfaSecret, AuthProvider authProvider, String providerId) {
        this.id = id != null ? id : UUID.randomUUID();
        this.email = Objects.requireNonNull(email, "Email no puede ser nulo");
        this.password = password != null ? password : Password.oauthDummy(); // Para usuarios OAuth puede no requerir password local
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.companyName = companyName;
        this.ruc = ruc;
        this.legalRepresentativeName = legalRepresentativeName;
        this.role = role != null ? role : "N";
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.authProvider = authProvider != null ? authProvider : AuthProvider.LOCAL;
        this.providerId = providerId;
    }

    public UUID getId() { return id; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Dni getDni() { return dni; }
    public String getCompanyName() { return companyName; }
    public Ruc getRuc() { return ruc; }
    public String getLegalRepresentativeName() { return legalRepresentativeName; }
    public String getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isMfaEnabled() { return mfaEnabled; }
    public String getMfaSecret() { return mfaSecret; }
    public AuthProvider getAuthProvider() { return authProvider; }
    public String getProviderId() { return providerId; }

    public void updatePassword(Password newPassword) {
        this.password = Objects.requireNonNull(newPassword, "Nueva contraseña requerida");
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void enableMfa(String secret) {
        this.mfaEnabled = true;
        this.mfaSecret = Objects.requireNonNull(secret, "El secreto MFA no puede ser nulo");
        this.updatedAt = LocalDateTime.now();
    }

    public void disableMfa() {
        this.mfaEnabled = false;
        this.mfaSecret = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void linkOAuthProvider(AuthProvider provider, String providerId) {
        this.authProvider = Objects.requireNonNull(provider, "Proveedor OAuth no puede ser nulo");
        this.providerId = Objects.requireNonNull(providerId, "ProviderId no puede ser nulo");
        this.updatedAt = LocalDateTime.now();
    }

    // Static Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Email email;
        private Password password;
        private String firstName;
        private String lastName;
        private Dni dni;
        private String companyName;
        private Ruc ruc;
        private String legalRepresentativeName;
        private String role;
        private UserStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean mfaEnabled = false;
        private String mfaSecret;
        private AuthProvider authProvider = AuthProvider.LOCAL;
        private String providerId;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder email(Email email) { this.email = email; return this; }
        public Builder password(Password password) { this.password = password; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder dni(Dni dni) { this.dni = dni; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder ruc(Ruc ruc) { this.ruc = ruc; return this; }
        public Builder legalRepresentativeName(String legalRepresentativeName) { this.legalRepresentativeName = legalRepresentativeName; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder status(UserStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder mfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; return this; }
        public Builder mfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; return this; }
        public Builder authProvider(AuthProvider authProvider) { this.authProvider = authProvider; return this; }
        public Builder providerId(String providerId) { this.providerId = providerId; return this; }

        public User build() {
            return new User(id, email, password, firstName, lastName, dni, companyName, ruc, legalRepresentativeName, role, status, createdAt, updatedAt, mfaEnabled, mfaSecret, authProvider, providerId);
        }
    }
}
