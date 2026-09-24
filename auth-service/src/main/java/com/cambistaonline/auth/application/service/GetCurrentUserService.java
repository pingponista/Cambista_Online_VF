package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.mappers.UserApplicationMapper;
import com.cambistaonline.auth.application.ports.inbound.GetCurrentUserUseCase;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserPersistencePort userPersistencePort;

    public GetCurrentUserService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public UserResponseDto execute(String emailStr) {
        Email email = new Email(emailStr);
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el correo: " + emailStr));

        return UserApplicationMapper.toResponseDto(user);
    }
}
