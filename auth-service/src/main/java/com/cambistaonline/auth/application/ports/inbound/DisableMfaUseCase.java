package com.cambistaonline.auth.application.ports.inbound;

import com.cambistaonline.auth.application.dto.DisableMfaCommand;

public interface DisableMfaUseCase {
    boolean execute(DisableMfaCommand command);
}
