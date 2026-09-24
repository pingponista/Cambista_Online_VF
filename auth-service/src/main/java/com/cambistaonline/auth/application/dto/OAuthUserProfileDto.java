package com.cambistaonline.auth.application.dto;

import com.cambistaonline.auth.domain.model.AuthProvider;

public class OAuthUserProfileDto {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String providerId;
    private final AuthProvider provider;

    public OAuthUserProfileDto(String email, String firstName, String lastName, String providerId, AuthProvider provider) {
        this.email = email;
        this.firstName = firstName != null ? firstName : "Usuario";
        this.lastName = lastName != null ? lastName : "";
        this.providerId = providerId;
        this.provider = provider;
    }

    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProviderId() { return providerId; }
    public AuthProvider getProvider() { return provider; }
}
