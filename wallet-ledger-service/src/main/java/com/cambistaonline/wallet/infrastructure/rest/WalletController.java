package com.cambistaonline.wallet.infrastructure.rest;

import com.cambistaonline.wallet.application.dto.WalletBalanceDto;
import com.cambistaonline.wallet.application.ports.inbound.GetWalletBalancesUseCase;
import com.cambistaonline.wallet.application.ports.inbound.ProvisionUserWalletsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet Service", description = "Gestión de Billeteras Multimoneda (PEN, USD, EUR)")
public class WalletController {

    private final GetWalletBalancesUseCase getWalletBalancesUseCase;
    private final ProvisionUserWalletsUseCase provisionUserWalletsUseCase;

    public WalletController(GetWalletBalancesUseCase getWalletBalancesUseCase,
                            ProvisionUserWalletsUseCase provisionUserWalletsUseCase) {
        this.getWalletBalancesUseCase = getWalletBalancesUseCase;
        this.provisionUserWalletsUseCase = provisionUserWalletsUseCase;
    }

    @GetMapping("/accounts")
    @Operation(summary = "Obtener saldos de billeteras del usuario", description = "Retorna el saldo disponible y reservado en PEN, USD y EUR.")
    public ResponseEntity<List<WalletBalanceDto>> getAccounts(@RequestParam("email") String email) {
        List<WalletBalanceDto> balances = getWalletBalancesUseCase.getBalancesByUser(email);
        if (balances.isEmpty()) {
            provisionUserWalletsUseCase.provisionInitialWallets(email);
            balances = getWalletBalancesUseCase.getBalancesByUser(email);
        }
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/provision")
    @Operation(summary = "Aprovisionar billeteras para nuevo usuario", description = "Crea automáticamente las 3 cuentas (PEN, USD, EUR) si no existen.")
    public ResponseEntity<Void> provisionAccounts(@RequestParam("email") String email) {
        provisionUserWalletsUseCase.provisionInitialWallets(email);
        return ResponseEntity.ok().build();
    }
}
