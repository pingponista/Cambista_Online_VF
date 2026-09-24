package com.cambistaonline.engine.infrastructure.rest;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;
import com.cambistaonline.engine.application.ports.inbound.ValidateExchangeRateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador Inbound RPC para la validación y aseguramiento síncrono de tasas durante la orquestación SAGA.
 */
@RestController
@RequestMapping("/internal/grpc/rate")
@Tag(name = "Exchange Rate Internal RPC", description = "Endpoints de validación y bloqueo de tasa síncrona")
public class RateInternalRpcController {

    private final ValidateExchangeRateUseCase validateExchangeRateUseCase;

    public RateInternalRpcController(ValidateExchangeRateUseCase validateExchangeRateUseCase) {
        this.validateExchangeRateUseCase = validateExchangeRateUseCase;
    }

    @PostMapping("/validate")
    @Operation(summary = "Paso SAGA: Validación síncrona de tasa garantizada", description = "Verifica que la tasa cotizada siga vigente dentro de la tolerancia permitida.")
    public ResponseEntity<ValidateRateResult> validateRate(@RequestBody ValidateRateQuery query) {
        ValidateRateResult result = validateExchangeRateUseCase.validateRate(query);
        return ResponseEntity.ok(result);
    }
}
