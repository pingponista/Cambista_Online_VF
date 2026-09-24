package com.cambistaonline.wallet.infrastructure.rest;

import com.cambistaonline.wallet.application.dto.UserPointsDto;
import com.cambistaonline.wallet.application.ports.inbound.AdjustUserPointsUseCase;
import com.cambistaonline.wallet.application.ports.inbound.GetUserPointsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets/points")
@Tag(name = "Loyalty Points Service", description = "Gestión de CambiPuntos del Usuario")
public class PointsController {

    private final GetUserPointsUseCase getUserPointsUseCase;
    private final AdjustUserPointsUseCase adjustUserPointsUseCase;

    public PointsController(GetUserPointsUseCase getUserPointsUseCase,
                            AdjustUserPointsUseCase adjustUserPointsUseCase) {
        this.getUserPointsUseCase = getUserPointsUseCase;
        this.adjustUserPointsUseCase = adjustUserPointsUseCase;
    }

    @GetMapping
    @Operation(summary = "Consultar saldo de CambiPuntos", description = "Retorna el saldo actual y los puntos acumulados históricos del usuario.")
    public ResponseEntity<UserPointsDto> getPoints(@RequestParam("email") String email) {
        return ResponseEntity.ok(getUserPointsUseCase.getUserPoints(email));
    }

    @PostMapping("/award")
    @Operation(summary = "Acreditar puntos por operación completada", description = "Añade puntos al balance del usuario.")
    public ResponseEntity<UserPointsDto> awardPoints(@RequestParam("email") String email, @RequestParam("points") int points) {
        return ResponseEntity.ok(adjustUserPointsUseCase.awardPoints(email, points));
    }

    @PostMapping("/redeem")
    @Operation(summary = "Canjear puntos para descuento", description = "Debita puntos del balance del usuario.")
    public ResponseEntity<UserPointsDto> redeemPoints(@RequestParam("email") String email, @RequestParam("points") int points) {
        return ResponseEntity.ok(adjustUserPointsUseCase.redeemPoints(email, points));
    }
}
