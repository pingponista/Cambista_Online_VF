package com.cambistaonline.auth.application.usecases;

import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.mappers.UserApplicationMapper;
import com.cambistaonline.auth.domain.exceptions.UserNotFoundException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Email;

public class GetCurrentUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetCurrentUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public UserResponseDto execute(String emailStr) {
        Email email = new Email(emailStr);
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el correo: " + emailStr));

        return UserApplicationMapper.toResponseDto(user);
    }
}
