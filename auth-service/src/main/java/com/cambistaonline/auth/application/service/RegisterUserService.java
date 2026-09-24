package com.cambistaonline.auth.application.service;

import com.cambistaonline.auth.application.dto.RegisterUserCommand;
import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.mappers.UserApplicationMapper;
import com.cambistaonline.auth.application.ports.inbound.RegisterUserUseCase;
import com.cambistaonline.auth.application.ports.outbound.PasswordEncoderPort;
import com.cambistaonline.auth.application.ports.outbound.UserEventPublisherPort;
import com.cambistaonline.auth.application.ports.outbound.UserNotificationPort;
import com.cambistaonline.auth.application.ports.outbound.UserPersistencePort;
import com.cambistaonline.auth.domain.events.UserRegisteredEvent;
import com.cambistaonline.auth.domain.exceptions.UserAlreadyExistsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.model.UserStatus;
import com.cambistaonline.auth.domain.valueobjects.Dni;
import com.cambistaonline.auth.domain.valueobjects.Email;
import com.cambistaonline.auth.domain.valueobjects.Password;
import com.cambistaonline.auth.domain.valueobjects.Ruc;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserEventPublisherPort userEventPublisherPort;
    private final UserNotificationPort userNotificationPort;

    public RegisterUserService(UserPersistencePort userPersistencePort,
                               PasswordEncoderPort passwordEncoderPort,
                               UserEventPublisherPort userEventPublisherPort,
                               UserNotificationPort userNotificationPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userEventPublisherPort = userEventPublisherPort;
        this.userNotificationPort = userNotificationPort;
    }

    @Override
    public UserResponseDto execute(RegisterUserCommand command) {
        Email email = new Email(command.getEmail());

        if (userPersistencePort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "El correo electrónico " + command.getEmail() + " ya se encuentra registrado.");
        }

        Password rawPassword = new Password(command.getPassword());
        String encodedHash = passwordEncoderPort.encode(rawPassword.getValue());
        Password hashedPassword = Password.fromHash(encodedHash);

        Dni dni = command.getDni() != null && !command.getDni().isBlank()
                ? new Dni(command.getDni()) : null;
        Ruc ruc = command.getRuc() != null && !command.getRuc().isBlank()
                ? new Ruc(command.getRuc()) : null;

        String role = command.getRole() != null ? command.getRole() : "N";
        LocalDateTime now = LocalDateTime.now();

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
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userPersistencePort.save(newUser);

        // ── Publicar evento de dominio → Kafka (auditoría)
        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId().toString(),
                savedUser.getEmail().getValue(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );
        userEventPublisherPort.publishUserRegistered(event);

        // ── Enviar notificación → RabbitMQ (email de bienvenida simulado)
        userNotificationPort.notifyWelcome(event);

        return UserApplicationMapper.toResponseDto(savedUser);
    }
}
