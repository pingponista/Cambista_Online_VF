package com.cambistaonline.auth.application.ports.outbound;

import com.cambistaonline.auth.domain.model.User;

public interface JwtTokenPort {
    String generateToken(User user);
    String extractUsername(String token);
    boolean validateToken(String token);
    long getExpirationSeconds();

    String generateMfaSessionToken(String email);
    String validateMfaSessionToken(String token);
}
