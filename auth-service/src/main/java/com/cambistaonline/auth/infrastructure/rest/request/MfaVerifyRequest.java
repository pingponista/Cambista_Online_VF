package com.cambistaonline.auth.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MfaVerifyRequest {

    @NotBlank(message = "El token de sesión MFA es obligatorio")
    private String mfaSessionToken;

    @NotBlank(message = "El código TOTP es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener exactamente 6 dígitos")
    private String code;

    public MfaVerifyRequest() {}

    public MfaVerifyRequest(String mfaSessionToken, String code) {
        this.mfaSessionToken = mfaSessionToken;
        this.code = code;
    }

    public String getMfaSessionToken() { return mfaSessionToken; }
    public void setMfaSessionToken(String mfaSessionToken) { this.mfaSessionToken = mfaSessionToken; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
