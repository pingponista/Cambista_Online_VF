package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.OAuthAuthCommand;
import com.cambistaonline.auth.application.dto.OAuthUserProfileDto;
import com.cambistaonline.auth.application.ports.outbound.JwtTokenPort;
import com.cambistaonline.auth.application.ports.outbound.OAuthClientPort;
import com.cambistaonline.auth.application.ports.outbound.UserEventPublisherPort;
import com.cambistaonline.auth.application.ports.outbound.UserNotificationPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.model.AuthProvider;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticateOAuthUserServiceTest {

    private OAuthClientPort googleClient;
    private UserPersistencePort userPersistencePort;
    private JwtTokenPort jwtTokenPort;
    private UserEventPublisherPort userEventPublisherPort;
    private UserNotificationPort userNotificationPort;
    private AuthenticateOAuthUserService service;

    @BeforeEach
    void setUp() {
        googleClient = mock(OAuthClientPort.class);
        userPersistencePort = mock(UserPersistencePort.class);
        jwtTokenPort = mock(JwtTokenPort.class);
        userEventPublisherPort = mock(UserEventPublisherPort.class);
        userNotificationPort = mock(UserNotificationPort.class);

        when(googleClient.supports(AuthProvider.GOOGLE)).thenReturn(true);
        when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service = new AuthenticateOAuthUserService(
                List.of(googleClient),
                userPersistencePort,
                jwtTokenPort,
                userEventPublisherPort,
                userNotificationPort
        );
    }

    @Test
    @DisplayName("Debe registrar usuario nuevo y retornar JWT si el usuario no existe")
    void shouldRegisterAndReturnJwtForNewUser() {
        OAuthAuthCommand command = new OAuthAuthCommand(AuthProvider.GOOGLE, "mock_code", "http://localhost:3000");
        OAuthUserProfileDto profile = new OAuthUserProfileDto("nuevo@gmail.com", "Nuevo", "Usuario", "goog-1", AuthProvider.GOOGLE);

        when(googleClient.getUserProfile("mock_code", "http://localhost:3000")).thenReturn(profile);
        when(userPersistencePort.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        when(userPersistencePort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenPort.generateToken(any(User.class))).thenReturn("jwt-token-valido");
        when(jwtTokenPort.getExpirationSeconds()).thenReturn(3600L);

        AuthTokenResponseDto response = service.execute(command);

        assertNotNull(response);
        assertFalse(response.isMfaRequired());
        assertEquals("jwt-token-valido", response.getAccessToken());
        verify(userPersistencePort).save(any(User.class));
        verify(userEventPublisherPort).publishUserRegistered(any());
        verify(userNotificationPort).notifyWelcome(any());
    }

    @Test
    @DisplayName("Debe solicitar MFA si el usuario existente tiene MFA activado")
    void shouldRequireMfaWhenUserHasMfaEnabled() {
        OAuthAuthCommand command = new OAuthAuthCommand(AuthProvider.GOOGLE, "mock_code", "http://localhost:3000");
        OAuthUserProfileDto profile = new OAuthUserProfileDto("mfa_user@gmail.com", "Mfa", "User", "goog-2", AuthProvider.GOOGLE);

        User userWithMfa = User.builder()
                .email(new Email("mfa_user@gmail.com"))
                .mfaEnabled(true)
                .mfaSecret("SECRET")
                .build();

        when(googleClient.getUserProfile("mock_code", "http://localhost:3000")).thenReturn(profile);
        when(userPersistencePort.findByEmail(any(Email.class))).thenReturn(Optional.of(userWithMfa));
        when(jwtTokenPort.generateMfaSessionToken("mfa_user@gmail.com")).thenReturn("temp-mfa-token");

        AuthTokenResponseDto response = service.execute(command);

        assertNotNull(response);
        assertTrue(response.isMfaRequired());
        assertEquals("temp-mfa-token", response.getMfaSessionToken());
        assertNull(response.getAccessToken());
    }
}
