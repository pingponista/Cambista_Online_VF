package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.SetupMfaResponseDto;

public interface SetupMfaUseCase {
    SetupMfaResponseDto execute(String userEmail);
}
