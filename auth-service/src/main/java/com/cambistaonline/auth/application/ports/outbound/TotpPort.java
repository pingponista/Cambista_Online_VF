package com.cambistaonline.auth.application.ports.outbound;

public interface TotpPort {
    String generateSecret();
    String getOtpAuthUri(String secret, String accountName, String issuer);
    boolean verifyCode(String secret, String code);
}
