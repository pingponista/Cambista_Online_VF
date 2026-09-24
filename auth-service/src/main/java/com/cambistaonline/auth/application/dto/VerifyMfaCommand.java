package com.cambistaonline.auth.application.dto;

public class VerifyMfaCommand {
    private final String mfaSessionToken;
    private final String code;

    public VerifyMfaCommand(String mfaSessionToken, String code) {
        this.mfaSessionToken = mfaSessionToken;
        this.code = code;
    }

    public String getMfaSessionToken() { return mfaSessionToken; }
    public String getCode() { return code; }
}
