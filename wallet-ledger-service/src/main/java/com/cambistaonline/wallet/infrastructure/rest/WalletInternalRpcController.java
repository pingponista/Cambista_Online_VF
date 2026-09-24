package com.cambistaonline.wallet.infrastructure.rest;

import com.cambistaonline.common.grpc.wallet.*;
import com.cambistaonline.wallet.application.ports.inbound.LockFundsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.SettleFundsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.UnlockFundsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada Inbound RPC / Internal Endpoint para orquestación SAGA interservicios.
 * Implementa los contratos definidos en common-proto (Lock, Unlock/Compensate, Settle).
 */
@RestController
@RequestMapping("/internal/grpc/wallet")
@Tag(name = "Wallet Internal RPC", description = "Endpoints de baja latencia para coordinación SAGA (Lock/Unlock/Settle)")
public class WalletInternalRpcController {

    private static final Logger log = LoggerFactory.getLogger(WalletInternalRpcController.class);

    private final LockFundsUseCase lockFundsUseCase;
    private final UnlockFundsUseCase unlockFundsUseCase;
    private final SettleFundsUseCase settleFundsUseCase;

    public WalletInternalRpcController(LockFundsUseCase lockFundsUseCase,
                                       UnlockFundsUseCase unlockFundsUseCase,
                                       SettleFundsUseCase settleFundsUseCase) {
        this.lockFundsUseCase = lockFundsUseCase;
        this.unlockFundsUseCase = unlockFundsUseCase;
        this.settleFundsUseCase = settleFundsUseCase;
    }

    @PostMapping("/lock")
    @Operation(summary = "Paso SAGA: Bloqueo de fondos de saldo origen")
    public ResponseEntity<LockFundsResult> lockFunds(@RequestBody LockFundsCommand command) {
        log.info("[INTERNAL-RPC] Recibido bloqueo para trx: {}", command.transactionId());
        LockFundsResult result = lockFundsUseCase.lockFunds(command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/unlock")
    @Operation(summary = "Compensación SAGA: Desbloqueo y restitución de fondos retenidos")
    public ResponseEntity<UnlockFundsResult> unlockFunds(@RequestBody UnlockFundsCommand command) {
        log.warn("[INTERNAL-RPC COMPENSACIÓN] Recibido desbloqueo compensatorio para trx: {}", command.transactionId());
        UnlockFundsResult result = unlockFundsUseCase.unlockFunds(command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/settle")
    @Operation(summary = "Paso SAGA: Liquidación final multimoneda y fidelización")
    public ResponseEntity<SettleFundsResult> settleFunds(@RequestBody SettleFundsCommand command) {
        log.info("[INTERNAL-RPC] Recibida liquidación final para trx: {}", command.transactionId());
        SettleFundsResult result = settleFundsUseCase.settleFunds(command);
        return ResponseEntity.ok(result);
    }
}
