package com.cambistaonline.auth.application.dto;

public class EnableMfaCommand {
    private final String userEmail;
    private final String code;

    public EnableMfaCommand(String userEmail, String code) {
        this.userEmail = userEmail;
        this.code = code;
    }

    public String getUserEmail() { return userEmail; }
    public String getCode() { return code; }
}
