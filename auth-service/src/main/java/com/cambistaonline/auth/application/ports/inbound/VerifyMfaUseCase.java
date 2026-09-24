package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.VerifyMfaCommand;

public interface VerifyMfaUseCase {
    AuthTokenResponseDto execute(VerifyMfaCommand command);
}
