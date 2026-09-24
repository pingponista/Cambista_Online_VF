package com.cambistaonline.auth.application.dto;

public class AuthTokenResponseDto {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private boolean mfaRequired;
    private String mfaSessionToken;

    public AuthTokenResponseDto(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.mfaRequired = false;
        this.mfaSessionToken = null;
    }

    public static AuthTokenResponseDto mfaRequired(String mfaSessionToken) {
        AuthTokenResponseDto dto = new AuthTokenResponseDto(null, null, 0);
        dto.mfaRequired = true;
        dto.mfaSessionToken = mfaSessionToken;
        return dto;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public boolean isMfaRequired() { return mfaRequired; }
    public String getMfaSessionToken() { return mfaSessionToken; }
}
