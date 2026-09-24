package com.cambistaonline.auth.infrastructure.config;

import com.cambistaonline.auth.application.ports.inbound.*;
import com.cambistaonline.auth.application.ports.outbound.*;
import com.cambistaonline.auth.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            UserEventPublisherPort userEventPublisherPort,
            UserNotificationPort userNotificationPort
    ) {
        return new RegisterUserService(
                userPersistencePort,
                passwordEncoderPort,
                userEventPublisherPort,
                userNotificationPort
        );
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserPersistencePort userPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtTokenPort jwtTokenPort
    ) {
        return new AuthenticateUserService(userPersistencePort, passwordEncoderPort, jwtTokenPort);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(
            UserPersistencePort userPersistencePort
    ) {
        return new GetCurrentUserService(userPersistencePort);
    }

    @Bean
    public AuthenticateOAuthUserUseCase authenticateOAuthUserUseCase(
            List<OAuthClientPort> oauthClients,
            UserPersistencePort userPersistencePort,
            JwtTokenPort jwtTokenPort,
            UserEventPublisherPort userEventPublisherPort,
            UserNotificationPort userNotificationPort
    ) {
        return new AuthenticateOAuthUserService(
                oauthClients,
                userPersistencePort,
                jwtTokenPort,
                userEventPublisherPort,
                userNotificationPort
        );
    }

    @Bean
    public SetupMfaUseCase setupMfaUseCase(
            UserPersistencePort userPersistencePort,
            TotpPort totpPort
    ) {
        return new SetupMfaService(userPersistencePort, totpPort);
    }

    @Bean
    public EnableMfaUseCase enableMfaUseCase(
            UserPersistencePort userPersistencePort,
            TotpPort totpPort
    ) {
        return new EnableMfaService(userPersistencePort, totpPort);
    }

    @Bean
    public VerifyMfaUseCase verifyMfaUseCase(
            UserPersistencePort userPersistencePort,
            TotpPort totpPort,
            JwtTokenPort jwtTokenPort
    ) {
        return new VerifyMfaService(userPersistencePort, totpPort, jwtTokenPort);
    }

    @Bean
    public DisableMfaUseCase disableMfaUseCase(
            UserPersistencePort userPersistencePort,
            TotpPort totpPort
    ) {
        return new DisableMfaService(userPersistencePort, totpPort);
    }
}
