package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.VerifyMfaCommand;
import com.cambistaonline.auth.application.ports.inbound.VerifyMfaUseCase;
import com.cambistaonline.auth.application.ports.outbound.JwtTokenPort;
import com.cambistaonline.auth.application.ports.outbound.TotpPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class VerifyMfaService implements VerifyMfaUseCase {

    private final UserPersistencePort userPersistencePort;
    private final TotpPort totpPort;
    private final JwtTokenPort jwtTokenPort;

    public VerifyMfaService(UserPersistencePort userPersistencePort, TotpPort totpPort, JwtTokenPort jwtTokenPort) {
        this.userPersistencePort = userPersistencePort;
        this.totpPort = totpPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public AuthTokenResponseDto execute(VerifyMfaCommand command) {
        String emailStr = jwtTokenPort.validateMfaSessionToken(command.getMfaSessionToken());
        if (emailStr == null || emailStr.isBlank()) {
            throw new InvalidCredentialsException("Sesión MFA inválida o expirada. Por favor vuelva a iniciar sesión.");
        }

        Email email = new Email(emailStr);
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            throw new InvalidCredentialsException("El usuario no tiene MFA habilitado.");
        }

        boolean valid = totpPort.verifyCode(user.getMfaSecret(), command.getCode());
        if (!valid) {
            throw new InvalidCredentialsException("Código de autenticación de Google Authenticator inválido.");
        }

        String jwtToken = jwtTokenPort.generateToken(user);
        long expiresIn = jwtTokenPort.getExpirationSeconds();
        return new AuthTokenResponseDto(jwtToken, "Bearer", expiresIn);
    }
}
