package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.VerifyMfaCommand;
import com.cambistaonline.auth.application.ports.outbound.JwtTokenPort;
import com.cambistaonline.auth.application.ports.outbound.TotpPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerifyMfaServiceTest {

    private UserPersistencePort userPersistencePort;
    private TotpPort totpPort;
    private JwtTokenPort jwtTokenPort;
    private VerifyMfaService service;

    @BeforeEach
    void setUp() {
        userPersistencePort = mock(UserPersistencePort.class);
        totpPort = mock(TotpPort.class);
        jwtTokenPort = mock(JwtTokenPort.class);
        service = new VerifyMfaService(userPersistencePort, totpPort, jwtTokenPort);
    }

    @Test
    @DisplayName("Debe validar código TOTP y entregar JWT final")
    void shouldVerifyCodeAndReturnFinalJwt() {
        VerifyMfaCommand command = new VerifyMfaCommand("valid-mfa-session", "123456");
        User user = User.builder()
                .email(new Email("user@domain.com"))
                .mfaEnabled(true)
                .mfaSecret("SECRET32")
                .build();

        when(jwtTokenPort.validateMfaSessionToken("valid-mfa-session")).thenReturn("user@domain.com");
        when(userPersistencePort.findByEmail(new Email("user@domain.com"))).thenReturn(Optional.of(user));
        when(totpPort.verifyCode("SECRET32", "123456")).thenReturn(true);
        when(jwtTokenPort.generateToken(user)).thenReturn("final-jwt");
        when(jwtTokenPort.getExpirationSeconds()).thenReturn(3600L);

        AuthTokenResponseDto response = service.execute(command);

        assertNotNull(response);
        assertEquals("final-jwt", response.getAccessToken());
        assertFalse(response.isMfaRequired());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el código TOTP es incorrecto")
    void shouldThrowWhenCodeIsInvalid() {
        VerifyMfaCommand command = new VerifyMfaCommand("valid-mfa-session", "999999");
        User user = User.builder()
                .email(new Email("user@domain.com"))
                .mfaEnabled(true)
                .mfaSecret("SECRET32")
                .build();

        when(jwtTokenPort.validateMfaSessionToken("valid-mfa-session")).thenReturn("user@domain.com");
        when(userPersistencePort.findByEmail(new Email("user@domain.com"))).thenReturn(Optional.of(user));
        when(totpPort.verifyCode("SECRET32", "999999")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> service.execute(command));
    }
}
