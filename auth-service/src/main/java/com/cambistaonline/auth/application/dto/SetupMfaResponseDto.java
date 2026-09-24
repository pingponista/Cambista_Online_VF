package com.cambistaonline.auth.application.dto;

public class SetupMfaResponseDto {
    private final String secret;
    private final String qrCodeUri;
    private final String manualKey;

    public SetupMfaResponseDto(String secret, String qrCodeUri, String manualKey) {
        this.secret = secret;
        this.qrCodeUri = qrCodeUri;
        this.manualKey = manualKey;
    }

    public String getSecret() { return secret; }
    public String getQrCodeUri() { return qrCodeUri; }
    public String getManualKey() { return manualKey; }
}
