package com.cambistaonline.auth.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;

public class OAuthLoginRequest {

    @NotBlank(message = "El código de autorización OAuth es obligatorio")
    private String code;

    private String redirectUri;

    public OAuthLoginRequest() {}

    public OAuthLoginRequest(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
