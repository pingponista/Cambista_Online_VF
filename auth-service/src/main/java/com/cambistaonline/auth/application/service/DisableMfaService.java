package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.DisableMfaCommand;
import com.cambistaonline.auth.application.ports.inbound.DisableMfaUseCase;
import com.cambistaonline.auth.application.ports.outbound.TotpPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.InvalidCredentialsException;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class DisableMfaService implements DisableMfaUseCase {

    private final UserPersistencePort userPersistencePort;
    private final TotpPort totpPort;

    public DisableMfaService(UserPersistencePort userPersistencePort, TotpPort totpPort) {
        this.userPersistencePort = userPersistencePort;
        this.totpPort = totpPort;
    }

    @Override
    public boolean execute(DisableMfaCommand command) {
        Email email = new Email(command.getUserEmail());
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + command.getUserEmail()));

        if (!user.isMfaEnabled()) {
            return true;
        }

        boolean valid = totpPort.verifyCode(user.getMfaSecret(), command.getCode());
        if (!valid) {
            throw new InvalidCredentialsException("Código de autenticación inválido. No se pudo desactivar MFA.");
        }

        user.disableMfa();
        userPersistencePort.save(user);
        return true;
    }
}
