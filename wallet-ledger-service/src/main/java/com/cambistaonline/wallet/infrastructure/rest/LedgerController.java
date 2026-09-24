package com.cambistaonline.wallet.infrastructure.rest;

import com.cambistaonline.wallet.application.dto.LedgerEntryDto;
import com.cambistaonline.wallet.application.dto.LedgerMovementCommand;
import com.cambistaonline.wallet.application.ports.inbound.ProcessLedgerMovementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets/ledger")
@Tag(name = "Ledger Service", description = "Libro Mayor Contable Inmutable (Double-Entry Ledger)")
public class LedgerController {

    private final ProcessLedgerMovementUseCase processLedgerMovementUseCase;

    public LedgerController(ProcessLedgerMovementUseCase processLedgerMovementUseCase) {
        this.processLedgerMovementUseCase = processLedgerMovementUseCase;
    }

    @PostMapping("/movement")
    @Operation(summary = "Registrar movimiento contable", description = "Ejecuta un débito o crédito en el saldo de la billetera y genera la entrada inmutable en el ledger.")
    public ResponseEntity<LedgerEntryDto> recordMovement(@Valid @RequestBody LedgerMovementCommand command) {
        LedgerEntryDto entry = processLedgerMovementUseCase.processMovement(command);
        return ResponseEntity.ok(entry);
    }
}
