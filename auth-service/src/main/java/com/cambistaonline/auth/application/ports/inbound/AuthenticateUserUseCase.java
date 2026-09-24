package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.AuthenticateUserCommand;
import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;

public interface AuthenticateUserUseCase {
    AuthTokenResponseDto execute(AuthenticateUserCommand command);
}
