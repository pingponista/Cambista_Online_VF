package com.cambistaonline.auth.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MfaCodeRequest {

    @NotBlank(message = "El código TOTP es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener exactamente 6 dígitos")
    private String code;

    public MfaCodeRequest() {}

    public MfaCodeRequest(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
