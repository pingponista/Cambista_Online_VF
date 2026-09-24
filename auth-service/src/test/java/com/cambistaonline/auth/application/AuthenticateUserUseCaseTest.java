package com.cambistaonline.auth.application;

import com.cambistaonline.auth.application.dto.AuthenticateUserCommand;
import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.usecases.AuthenticateUserUseCase;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.ports.JwtTokenPort;
import com.cambistaonline.auth.domain.ports.PasswordEncoderPort;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Email;
import com.cambistaonline.auth.domain.valueobjects.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtTokenPort jwtTokenPort;

    private AuthenticateUserUseCase authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        authenticateUserUseCase = new AuthenticateUserUseCase(userRepositoryPort, passwordEncoderPort, jwtTokenPort);
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        AuthenticateUserCommand command = new AuthenticateUserCommand("empresa@test.com", "Password123*");

        User user = User.builder()
                .email(new Email("empresa@test.com"))
                .password(Password.fromHash("$2a$10$hashedPassword"))
                .role("J")
                .build();

        when(userRepositoryPort.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(eq("Password123*"), eq("$2a$10$hashedPassword"))).thenReturn(true);
        when(jwtTokenPort.generateToken(any())).thenReturn("mocked-jwt-token");
        when(jwtTokenPort.getExpirationSeconds()).thenReturn(3600L);

        AuthTokenResponseDto response = authenticateUserUseCase.execute(command);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordDoesNotMatch() {
        AuthenticateUserCommand command = new AuthenticateUserCommand("empresa@test.com", "WrongPassword");

        User user = User.builder()
                .email(new Email("empresa@test.com"))
                .password(Password.fromHash("$2a$10$hashedPassword"))
                .role("J")
                .build();

        when(userRepositoryPort.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(eq("WrongPassword"), eq("$2a$10$hashedPassword"))).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticateUserUseCase.execute(command));
    }
}
