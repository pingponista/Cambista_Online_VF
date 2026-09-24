package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.EnableMfaCommand;

public interface EnableMfaUseCase {
    boolean execute(EnableMfaCommand command);
}
