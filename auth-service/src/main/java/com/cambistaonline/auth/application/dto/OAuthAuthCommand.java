package com.cambistaonline.auth.application.dto;

import com.cambistaonline.auth.domain.model.AuthProvider;

public class OAuthAuthCommand {
    private final AuthProvider provider;
    private final String code;
    private final String redirectUri;

    public OAuthAuthCommand(AuthProvider provider, String code, String redirectUri) {
        this.provider = provider;
        this.code = code;
        this.redirectUri = redirectUri;
    }

    public AuthProvider getProvider() { return provider; }
    public String getCode() { return code; }
    public String getRedirectUri() { return redirectUri; }
}
