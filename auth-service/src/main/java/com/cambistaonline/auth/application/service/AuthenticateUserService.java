package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.AuthenticateUserCommand;
import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.ports.inbound.AuthenticateUserUseCase;
import com.cambistaonline.auth.application.ports.outbound.JwtTokenPort;
import com.cambistaonline.auth.application.ports.outbound.PasswordEncoderPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtTokenPort jwtTokenPort;

    public AuthenticateUserService(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort, JwtTokenPort jwtTokenPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public AuthTokenResponseDto execute(AuthenticateUserCommand command) {
        Email email = new Email(command.getEmail());

        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales de acceso inválidas."));

        if (!passwordEncoderPort.matches(command.getPassword(), user.getPassword().getValue())) {
            throw new InvalidCredentialsException("Credenciales de acceso inválidas.");
        }

        // Si el usuario tiene MFA activado, requerir segundo factor
        if (user.isMfaEnabled()) {
            String mfaSessionToken = jwtTokenPort.generateMfaSessionToken(user.getEmail().getValue());
            return AuthTokenResponseDto.mfaRequired(mfaSessionToken);
        }

        String jwtToken = jwtTokenPort.generateToken(user);
        long expiresIn = jwtTokenPort.getExpirationSeconds();

        return new AuthTokenResponseDto(jwtToken, "Bearer", expiresIn);
    }
}
