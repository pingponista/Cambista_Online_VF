package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.RegisterUserCommand;
import com.cambistaonline.auth.application.dto.UserResponseDto;

public interface RegisterUserUseCase {
    UserResponseDto execute(RegisterUserCommand command);
}
