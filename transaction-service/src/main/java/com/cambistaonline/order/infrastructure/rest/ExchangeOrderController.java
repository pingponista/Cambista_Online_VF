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
    public ResponseEntity<OrderApiResponseDto<CreateOrderResponse>> createOrder(@RequestBody(required = false) CreateOrderRequest request) {
        CreateOrderRequest req = request != null ? request : new CreateOrderRequest();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null) ? auth.getName() : "demo@cambistaonline.pe";

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
    public ResponseEntity<OrderApiResponseDto<List<OrderSummaryDto>>> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null) ? auth.getName() : "demo@cambistaonline.pe";

        List<OrderSummaryDto> orders = getMyOrdersUseCase.execute(userEmail);
        return ResponseEntity.ok(OrderApiResponseDto.ok("Operaciones obtenidas correctamente", orders));
    }

    @PostMapping("/{orderNumber}/confirm-transfer")
    @Operation(summary = "Confirmar Transferencia Bancaria", description = "Registra el comprobante/número de operación enviada por el usuario.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<OrderApiResponseDto<OrderSummaryDto>> confirmTransfer(
            @PathVariable String orderNumber,
            @RequestBody(required = false) Map<String, String> body) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (auth != null && auth.getName() != null) ? auth.getName() : "demo@cambistaonline.pe";
        String txNumber = (body != null && body.containsKey("transactionNumber")) ? body.get("transactionNumber") : "VOUCHER-001";

        OrderSummaryDto updatedOrder = confirmTransferUseCase.execute(orderNumber, userEmail, txNumber);
        return ResponseEntity.ok(OrderApiResponseDto.ok("Transferencia bancaria registrada exitosamente", updatedOrder));
    }
}
