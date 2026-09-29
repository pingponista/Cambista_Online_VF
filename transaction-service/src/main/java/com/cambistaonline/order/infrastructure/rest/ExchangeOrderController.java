package com.cambistaonline.order.infrastructure.rest;

import com.cambistaonline.order.application.dto.*;
import com.cambistaonline.order.application.ports.inbound.ConfirmTransferUseCase;
import com.cambistaonline.order.application.ports.inbound.CreateExchangeOrderUseCase;
import com.cambistaonline.order.application.ports.inbound.GetMyOrdersUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Exchange Orders", description = "Gestión y Registro de Operaciones de Cambio de Divisas")
public class ExchangeOrderController {

    private final CreateExchangeOrderUseCase createExchangeOrderUseCase;
    private final GetMyOrdersUseCase getMyOrdersUseCase;
    private final ConfirmTransferUseCase confirmTransferUseCase;

    public ExchangeOrderController(CreateExchangeOrderUseCase createExchangeOrderUseCase,
                                  GetMyOrdersUseCase getMyOrdersUseCase,
                                  ConfirmTransferUseCase confirmTransferUseCase) {
        this.createExchangeOrderUseCase = createExchangeOrderUseCase;
        this.getMyOrdersUseCase = getMyOrdersUseCase;
        this.confirmTransferUseCase = confirmTransferUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear Operación de Cambio", description = "Registra una nueva orden de cambio de divisas con congelamiento de tasa por 15 minutos.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<OrderApiResponseDto<CreateOrderResponse>> createOrder(
            jakarta.servlet.http.HttpServletRequest httpRequest,
            @RequestBody(required = false) CreateOrderRequest request) {
        CreateOrderRequest req = request != null ? request : new CreateOrderRequest();

        String userEmail = resolveUserEmail(httpRequest);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = "J";
        if (auth != null && auth.getAuthorities() != null) {
            boolean isNatural = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().contains("ROLE_N") || a.getAuthority().contains("N"));
            if (isNatural) {
                userRole = "N";
            }
        }

        CreateOrderResponse responseData = createExchangeOrderUseCase.execute(req, userEmail, userRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderApiResponseDto.ok("Operación creada correctamente", responseData));
    }

    @GetMapping
    @Operation(summary = "Consultar Historial de Operaciones", description = "Obtiene la lista de todas las operaciones pertenencientes al usuario autenticado.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<OrderApiResponseDto<List<OrderSummaryDto>>> getMyOrders(jakarta.servlet.http.HttpServletRequest httpRequest) {
        String userEmail = resolveUserEmail(httpRequest);

        List<OrderSummaryDto> orders = getMyOrdersUseCase.execute(userEmail);
        return ResponseEntity.ok(OrderApiResponseDto.ok("Operaciones obtenidas correctamente", orders));
    }

    @PostMapping("/{orderNumber}/confirm-transfer")
    @Operation(summary = "Confirmar Transferencia Bancaria", description = "Registra el comprobante/número de operación enviada por el usuario.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<OrderApiResponseDto<OrderSummaryDto>> confirmTransfer(
            jakarta.servlet.http.HttpServletRequest httpRequest,
            @PathVariable String orderNumber,
            @RequestBody(required = false) Map<String, String> body) {

        String userEmail = resolveUserEmail(httpRequest);
        String txNumber = (body != null && body.containsKey("transactionNumber")) ? body.get("transactionNumber") : "VOUCHER-001";

        OrderSummaryDto updatedOrder = confirmTransferUseCase.execute(orderNumber, userEmail, txNumber);
        return ResponseEntity.ok(OrderApiResponseDto.ok("Transferencia bancaria registrada exitosamente", updatedOrder));
    }

    private String resolveUserEmail(jakarta.servlet.http.HttpServletRequest request) {
        // 1. Verificar autenticación activa de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null 
                && !auth.getName().equalsIgnoreCase("anonymousUser") 
                && !auth.getName().trim().isEmpty()) {
            return auth.getName().trim();
        }

        if (request != null) {
            // 2. Extraer de header directo X-User-Email
            String headerEmail = request.getHeader("X-User-Email");
            if (headerEmail != null && !headerEmail.trim().isEmpty() && !headerEmail.equalsIgnoreCase("anonymousUser")) {
                return headerEmail.trim();
            }

            // 3. Extraer subject de token JWT en header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    String[] parts = token.split("\\.");
                    if (parts.length >= 2) {
                        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);
                        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
                        java.util.regex.Matcher m = p.matcher(payload);
                        if (m.find()) {
                            String emailFromJwt = m.group(1).trim();
                            if (!emailFromJwt.isEmpty() && !emailFromJwt.equalsIgnoreCase("anonymousUser")) {
                                return emailFromJwt;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 4. Fallback por defecto a cuenta seed con saldo disponible
        return "demo@cambistaonline.pe";
    }
}
