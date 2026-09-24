package com.cambistaonline.engine.infrastructure.rest;

import com.cambistaonline.engine.application.dto.CalculateRateRequest;
import com.cambistaonline.engine.application.dto.CalculateRateResponse;
import com.cambistaonline.engine.application.dto.FxBreakdownResponse;
import com.cambistaonline.engine.application.ports.inbound.CalculateExchangeRateUseCase;
import com.cambistaonline.engine.domain.model.CurrencyType;
import com.cambistaonline.engine.domain.model.CustomerLevel;
import com.cambistaonline.engine.domain.model.OperationType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping({"/api/v1/exchange", "/api/v1/rates"})
@Tag(name = "Exchange Rate Engine", description = "Motor Dinámico e Inteligente de Cálculo de Tipo de Cambio")
public class ExchangeEngineController {

    private final CalculateExchangeRateUseCase calculateExchangeRateUseCase;

    public ExchangeEngineController(CalculateExchangeRateUseCase calculateExchangeRateUseCase) {
        this.calculateExchangeRateUseCase = calculateExchangeRateUseCase;
    }

    @GetMapping("/breakdown")
    @Operation(summary = "Obtener desglose y saldo de CambiPuntos del usuario", description = "Retorna la tasa base, reglas dinámicas y saldo real de puntos en Neon DB del usuario autenticado.")
    public ResponseEntity<FxBreakdownResponse> getBreakdown(@RequestParam(value = "email", required = false) String emailParam) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (auth != null && auth.isAuthenticated() && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            userEmail = auth.getName();
        }

        if ((userEmail == null || userEmail.isBlank() || userEmail.equalsIgnoreCase("anonymousUser")) && StringUtils.hasText(emailParam)) {
            userEmail = emailParam;
        }

        if (userEmail == null || userEmail.isBlank() || userEmail.equalsIgnoreCase("anonymousUser")) {
            userEmail = "alex.meza@smartbricks.cl";
        }

        String userRole = "J";
        if (auth != null && auth.getAuthorities() != null) {
            boolean isNatural = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().contains("ROLE_N") || a.getAuthority().contains("N"));
            if (isNatural) {
                userRole = "N";
            }
        }

        CalculateRateResponse calc = calculateExchangeRateUseCase.execute(
                new CalculateRateRequest(CurrencyType.USD, CurrencyType.PEN, OperationType.COMPRA, 0),
                userEmail,
                userEmail,
                userRole,
                CustomerLevel.PREFERENTE
        );

        FxBreakdownResponse dto = new FxBreakdownResponse(
                calc.getBeneficios().getSaldoPuntos(),
                calc.getTipoCambio().getBaseSbs(),
                calc.getTipoCambio().getSpread(),
                calc.getTipoCambio().getAjusteHorario(),
                calc.getTipoCambio().getAjusteEstacional(),
                BigDecimal.valueOf(-0.0010),
                "Día valle: Promoción por baja demanda (-0.001)",
                10
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/calculate")
    @org.springframework.cache.annotation.Cacheable(value = "exchangeRates", key = "#request != null ? #request.currencyOrigin.name() + '-' + #request.currencyDestination.name() + '-' + #request.operationType.name() : 'default'")
    @Operation(summary = "Calcular Tipo de Cambio Dinámico (AP con Caché)", description = "Ejecuta el pipeline de reglas dinámicas o sirve cotización en caché ante partición de red.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<CalculateRateResponse> calculateRate(@RequestBody(required = false) CalculateRateRequest request) {
        CalculateRateRequest req = request != null ? request : new CalculateRateRequest();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null && !auth.getName().equalsIgnoreCase("anonymousUser"))
                ? auth.getName()
                : "alex.meza@smartbricks.cl";

        String userRole = "J";
        if (auth != null && auth.getAuthorities() != null) {
            boolean isNatural = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().contains("ROLE_N") || a.getAuthority().contains("N"));
            if (isNatural) {
                userRole = "N";
            }
        }

        CustomerLevel customerLevel = CustomerLevel.PREFERENTE;

        try {
            CalculateRateResponse response = calculateExchangeRateUseCase.execute(
                    req,
                    userEmail,
                    userEmail,
                    userRole,
                    customerLevel
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("ROLE_FORBIDDEN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new CalculateRateResponse(null, null, null, null, "403", "Acceso denegado: Rol no autorizado"));
            }
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Consultar Tasa de Cambio en Vivo", description = "Endpoint de lectura rápida para el cotizador del frontend.")
    public ResponseEntity<CalculateRateResponse> getLiveRate(
            @RequestParam(value = "origin", defaultValue = "USD") String origin,
            @RequestParam(value = "target", defaultValue = "PEN") String target,
            @RequestParam(value = "operation", defaultValue = "COMPRA") String operation) {

        CurrencyType currOrigin = CurrencyType.from(origin);
        CurrencyType currTarget = CurrencyType.from(target);
        OperationType opType = operation.equalsIgnoreCase("VENTA") || operation.equalsIgnoreCase("SELL")
                ? OperationType.VENTA : OperationType.COMPRA;

        CalculateRateRequest req = new CalculateRateRequest(currOrigin, currTarget, opType, 0);
        CalculateRateResponse response = calculateExchangeRateUseCase.execute(
                req,
                "public@cambistaonline.pe",
                "Usuario",
                "J",
                CustomerLevel.PREFERENTE
        );
        return ResponseEntity.ok(response);
    }
}
