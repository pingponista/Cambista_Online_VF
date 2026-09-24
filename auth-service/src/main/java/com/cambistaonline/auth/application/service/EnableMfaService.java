package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.EnableMfaCommand;
import com.cambistaonline.auth.application.ports.inbound.EnableMfaUseCase;
import com.cambistaonline.auth.application.ports.outbound.TotpPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class EnableMfaService implements EnableMfaUseCase {

    private final UserPersistencePort userPersistencePort;
    private final TotpPort totpPort;

    public EnableMfaService(UserPersistencePort userPersistencePort, TotpPort totpPort) {
        this.userPersistencePort = userPersistencePort;
        this.totpPort = totpPort;
    }

    @Override
    public boolean execute(EnableMfaCommand command) {
        Email email = new Email(command.getUserEmail());
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + command.getUserEmail()));

        String secret = user.getMfaSecret();
        if (secret == null || secret.isBlank()) {
            throw new InvalidCredentialsException("No hay una clave MFA pendiente de activación. Inicie el proceso de configuración primero.");
        }

        boolean valid = totpPort.verifyCode(secret, command.getCode());
        if (!valid) {
            throw new InvalidCredentialsException("Código de autenticación inválido o expirado.");
        }

        user.enableMfa(secret);
        userPersistencePort.save(user);
        return true;
    }
}
