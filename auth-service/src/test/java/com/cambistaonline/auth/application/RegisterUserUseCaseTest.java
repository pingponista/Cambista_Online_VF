package com.cambistaonline.auth.application;

import com.cambistaonline.auth.application.dto.RegisterUserCommand;
import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.usecases.RegisterUserUseCase;
import com.cambistaonline.auth.domain.exceptions.UserAlreadyExistsException;
import com.cambistaonline.auth.domain.model.User;
import com.cambistaonline.auth.domain.ports.PasswordEncoderPort;
import com.cambistaonline.auth.domain.ports.UserRepositoryPort;
import com.cambistaonline.auth.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCase(userRepositoryPort, passwordEncoderPort);
    }

    @Test
    void shouldRegisterPersonaNaturalSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "juan@gmail.com",
                "Password123*",
                "Juan",
                "Perez",
                "12345678",
                null,
                null,
                null,
                "N"
        );

        when(userRepositoryPort.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoderPort.encode(any())).thenReturn("hashedPassword123");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = registerUserUseCase.execute(command);

        assertNotNull(response);
        assertEquals("juan@gmail.com", response.getEmail());
        assertEquals("Juan", response.getFirstName());
        assertEquals("12345678", response.getDni());
        assertEquals("N", response.getRole());
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand(
                "juan@gmail.com",
                "Password123*",
                "Juan",
                "Perez",
                "12345678",
                null,
                null,
                null,
                "N"
        );

        when(userRepositoryPort.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> registerUserUseCase.execute(command));
        verify(userRepositoryPort, never()).save(any());
    }
}
