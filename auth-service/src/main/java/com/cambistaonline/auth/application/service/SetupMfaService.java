package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.SetupMfaResponseDto;
import com.cambistaonline.auth.application.ports.inbound.SetupMfaUseCase;
import com.cambistaonline.auth.application.ports.outbound.TotpPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class SetupMfaService implements SetupMfaUseCase {

    private final UserPersistencePort userPersistencePort;
    private final TotpPort totpPort;

    public SetupMfaService(UserPersistencePort userPersistencePort, TotpPort totpPort) {
        this.userPersistencePort = userPersistencePort;
        this.totpPort = totpPort;
    }

    @Override
    public SetupMfaResponseDto execute(String userEmail) {
        Email email = new Email(userEmail);
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userEmail));

        // Si el usuario ya tiene un secreto generado (activo o pendiente), reutilizarlo
        // para garantizar que su Google Authenticator no quede desincronizado.
        String secret;
        if (user.getMfaSecret() != null && !user.getMfaSecret().isBlank()) {
            secret = user.getMfaSecret();
        } else {
            secret = totpPort.generateSecret();
            User updated = User.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .dni(user.getDni())
                    .companyName(user.getCompanyName())
                    .ruc(user.getRuc())
                    .legalRepresentativeName(user.getLegalRepresentativeName())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .mfaEnabled(user.isMfaEnabled())
                    .mfaSecret(secret)
                    .authProvider(user.getAuthProvider())
                    .providerId(user.getProviderId())
                    .build();

            userPersistencePort.save(updated);
        }

        String qrCodeUri = totpPort.getOtpAuthUri(secret, user.getEmail().getValue(), "CambistaOnline");
        return new SetupMfaResponseDto(secret, qrCodeUri, secret);
    }
}
