package com.cambistaonline.auth.application.usecases;

import com.cambistaonline.auth.application.dto.AuthenticateUserCommand;
import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.ports.JwtTokenPort;
import com.cambistaonline.auth.domain.ports.PasswordEncoderPort;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class AuthenticateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtTokenPort jwtTokenPort;

    public AuthenticateUserUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort, JwtTokenPort jwtTokenPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    public AuthTokenResponseDto execute(AuthenticateUserCommand command) {
        Email email = new Email(command.getEmail());

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales de acceso inválidas."));

        if (!passwordEncoderPort.matches(command.getPassword(), user.getPassword().getValue())) {
            throw new InvalidCredentialsException("Credenciales de acceso inválidas.");
        }

        String jwtToken = jwtTokenPort.generateToken(user);
        long expiresIn = jwtTokenPort.getExpirationSeconds();

        return new AuthTokenResponseDto(jwtToken, "Bearer", expiresIn);
    }
}
