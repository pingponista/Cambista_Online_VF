package com.cambistaonline.wallet.application.ports.inbound;

import com.cambistaonline.wallet.application.dto.LedgerEntryDto;
import com.cambistaonline.wallet.application.dto.LedgerMovementCommand;

public interface ProcessLedgerMovementUseCase {
    LedgerEntryDto processMovement(LedgerMovementCommand command);
}
