package com.cambistaonline.auth.application.dto;

public class DisableMfaCommand {
    private final String userEmail;
    private final String code;

    public DisableMfaCommand(String userEmail, String code) {
        this.userEmail = userEmail;
        this.code = code;
    }

    public String getUserEmail() { return userEmail; }
    public String getCode() { return code; }
}
