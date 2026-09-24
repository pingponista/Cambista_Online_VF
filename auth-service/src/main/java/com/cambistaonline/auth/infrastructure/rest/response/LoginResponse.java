package com.cambistaonline.auth.infrastructure.rest.response;

public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private boolean mfaRequired;
    private String mfaSessionToken;

    public LoginResponse() {}

    public LoginResponse(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.mfaRequired = false;
        this.mfaSessionToken = null;
    }

    public static LoginResponse mfaRequired(String mfaSessionToken) {
        LoginResponse response = new LoginResponse();
        response.setMfaRequired(true);
        response.setMfaSessionToken(mfaSessionToken);
        return response;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public boolean isMfaRequired() { return mfaRequired; }
    public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }

    public String getMfaSessionToken() { return mfaSessionToken; }
    public void setMfaSessionToken(String mfaSessionToken) { this.mfaSessionToken = mfaSessionToken; }
}
