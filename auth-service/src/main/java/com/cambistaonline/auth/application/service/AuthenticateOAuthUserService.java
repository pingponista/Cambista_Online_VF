package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.OAuthAuthCommand;
import com.cambistaonline.auth.application.dto.OAuthUserProfileDto;
import com.cambistaonline.auth.application.ports.inbound.AuthenticateOAuthUserUseCase;
import com.cambistaonline.auth.application.ports.outbound.JwtTokenPort;
import com.cambistaonline.auth.application.ports.outbound.OAuthClientPort;
import com.cambistaonline.auth.application.ports.outbound.UserEventPublisherPort;
import com.cambistaonline.auth.application.ports.outbound.UserNotificationPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.events.UserRegisteredEvent;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.model.UserStatus;
import com.cambistaonline.auth.domain.valueobjects.Email;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthenticateOAuthUserService implements AuthenticateOAuthUserUseCase {

    private final List<OAuthClientPort> oauthClients;
    private final UserPersistencePort userPersistencePort;
    private final JwtTokenPort jwtTokenPort;
    private final UserEventPublisherPort userEventPublisherPort;
    private final UserNotificationPort userNotificationPort;

    public AuthenticateOAuthUserService(List<OAuthClientPort> oauthClients,
                                      UserPersistencePort userPersistencePort,
                                      JwtTokenPort jwtTokenPort,
                                      UserEventPublisherPort userEventPublisherPort,
                                      UserNotificationPort userNotificationPort) {
        this.oauthClients = oauthClients;
        this.userPersistencePort = userPersistencePort;
        this.jwtTokenPort = jwtTokenPort;
        this.userEventPublisherPort = userEventPublisherPort;
        this.userNotificationPort = userNotificationPort;
    }

    @Override
    public AuthTokenResponseDto execute(OAuthAuthCommand command) {
        OAuthClientPort client = oauthClients.stream()
                .filter(c -> c.supports(command.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Proveedor OAuth no soportado: " + command.getProvider()));

        OAuthUserProfileDto profile = client.getUserProfile(command.getCode(), command.getRedirectUri());
        if (profile == null || profile.getEmail() == null || profile.getEmail().isBlank()) {
            throw new InvalidCredentialsException("No se pudo obtener el correo electrónico del proveedor social.");
        }

        Email email = new Email(profile.getEmail());
        Optional<User> existingUserOpt = userPersistencePort.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            if (user.getProviderId() == null) {
                user.linkOAuthProvider(command.getProvider(), profile.getProviderId());
                User saved = userPersistencePort.save(user);
                if (saved != null) {
                    user = saved;
                }
            }
        } else {
            LocalDateTime now = LocalDateTime.now();
            user = User.builder()
                    .id(UUID.randomUUID())
                    .email(email)
                    .firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .role("N")
                    .status(UserStatus.ACTIVE)
                    .authProvider(command.getProvider())
                    .providerId(profile.getProviderId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            user = userPersistencePort.save(user);

            // Emitir evento de auditoría Kafka y notificación RabbitMQ
            UserRegisteredEvent event = new UserRegisteredEvent(
                    user.getId().toString(),
                    user.getEmail().getValue(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole()
            );
            if (userEventPublisherPort != null) {
                userEventPublisherPort.publishUserRegistered(event);
            }
            if (userNotificationPort != null) {
                userNotificationPort.notifyWelcome(event);
            }
        }

        // Si el usuario tiene MFA activado, exigir segundo factor
        if (user.isMfaEnabled()) {
            String mfaSessionToken = jwtTokenPort.generateMfaSessionToken(user.getEmail().getValue());
            return AuthTokenResponseDto.mfaRequired(mfaSessionToken);
        }

        String jwtToken = jwtTokenPort.generateToken(user);
        long expiresIn = jwtTokenPort.getExpirationSeconds();
        return new AuthTokenResponseDto(jwtToken, "Bearer", expiresIn);
    }
}
