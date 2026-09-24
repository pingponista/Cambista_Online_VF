package com.cambistaonline.auth.application.usecases;

import com.cambistaonline.auth.application.dto.RegisterUserCommand;
import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.mappers.UserApplicationMapper;
import com.cambistaonline.auth.domain.exceptions.UserAlreadyExistsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.model.UserStatus;
import com.cambistaonline.auth.domain.ports.PasswordEncoderPort;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Dni;
import com.cambistaonline.auth.domain.valueobjects.Email;
import com.cambistaonline.auth.domain.valueobjects.Password;
import com.cambistaonline.auth.domain.valueobjects.Ruc;

import java.util.UUID;

public class RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterUserUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public UserResponseDto execute(RegisterUserCommand command) {
        Email email = new Email(command.getEmail());

        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException("El correo electrónico " + command.getEmail() + " ya se encuentra registrado.");
        }

        // Valida complejidad de contraseña según reglas de negocio
        Password rawPassword = new Password(command.getPassword());
        String encodedHash = passwordEncoderPort.encode(rawPassword.getValue());
        Password hashedPassword = Password.fromHash(encodedHash);

        Dni dni = command.getDni() != null ? new Dni(command.getDni()) : null;
        Ruc ruc = command.getRuc() != null ? new Ruc(command.getRuc()) : null;

        String role = command.getRole() != null ? command.getRole() : "N";

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(hashedPassword)
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .dni(dni)
                .companyName(command.getCompanyName())
                .ruc(ruc)
                .legalRepresentativeName(command.getLegalRepresentativeName())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepositoryPort.save(newUser);
        return UserApplicationMapper.toResponseDto(savedUser);
    }
}
