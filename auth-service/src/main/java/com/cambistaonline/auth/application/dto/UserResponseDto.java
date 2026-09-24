package com.cambistaonline.auth.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponseDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private String companyName;
    private String ruc;
    private String legalRepresentativeName;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private boolean mfaEnabled;
    private String authProvider;

    public UserResponseDto() {}

    public UserResponseDto(UUID id, String email, String firstName, String lastName, String dni, String companyName, String ruc, String legalRepresentativeName, String role, String status, LocalDateTime createdAt) {
        this(id, email, firstName, lastName, dni, companyName, ruc, legalRepresentativeName, role, status, createdAt, false, "LOCAL");
    }

    public UserResponseDto(UUID id, String email, String firstName, String lastName, String dni, String companyName, String ruc, String legalRepresentativeName, String role, String status, LocalDateTime createdAt, boolean mfaEnabled, String authProvider) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.companyName = companyName;
        this.ruc = ruc;
        this.legalRepresentativeName = legalRepresentativeName;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.mfaEnabled = mfaEnabled;
        this.authProvider = authProvider;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDni() { return dni; }
    public String getCompanyName() { return companyName; }
    public String getRuc() { return ruc; }
    public String getLegalRepresentativeName() { return legalRepresentativeName; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public String getAuthProvider() { return authProvider; }
}
