package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.UserResponseDto;

public interface GetCurrentUserUseCase {
    UserResponseDto execute(String emailStr);
}
