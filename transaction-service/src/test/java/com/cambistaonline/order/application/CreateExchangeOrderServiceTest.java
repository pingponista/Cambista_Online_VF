package com.cambistaonline.order.application;

import com.cambistaonline.common.grpc.rate.ValidateRateQuery;
import com.cambistaonline.common.grpc.rate.ValidateRateResult;
import com.cambistaonline.common.grpc.wallet.*;
import com.cambistaonline.order.application.dto.CreateOrderRequest;
import com.cambistaonline.order.application.dto.CreateOrderResponse;
import com.cambistaonline.order.application.ports.outbound.ExchangeRateClientPort;
import com.cambistaonline.order.application.ports.outbound.OrderEventPublisherPort;
import com.cambistaonline.order.application.ports.outbound.OrderNotificationPort;
import com.cambistaonline.order.application.ports.outbound.WalletClientPort;
import com.cambistaonline.order.application.service.CreateExchangeOrderService;
import com.cambistaonline.order.application.service.ExchangeOrderSagaOrchestrator;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.ExchangeOrder;
import com.cambistaonline.order.domain.model.OperationType;
import com.cambistaonline.order.domain.ports.ExchangeOrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CreateExchangeOrderServiceTest {

    private ExchangeOrderRepositoryPort orderRepositoryPort;
    private ExchangeRateClientPort exchangeRateClientPort;
    private WalletClientPort walletClientPort;
    private OrderEventPublisherPort orderEventPublisherPort;
    private OrderNotificationPort orderNotificationPort;
    private ExchangeOrderSagaOrchestrator sagaOrchestrator;
    private CreateExchangeOrderService service;

    @BeforeEach
    void setUp() {
        orderRepositoryPort = Mockito.mock(ExchangeOrderRepositoryPort.class);
        exchangeRateClientPort = Mockito.mock(ExchangeRateClientPort.class);
        walletClientPort = Mockito.mock(WalletClientPort.class);
        orderEventPublisherPort = Mockito.mock(OrderEventPublisherPort.class);
        orderNotificationPort = Mockito.mock(OrderNotificationPort.class);

        sagaOrchestrator = new ExchangeOrderSagaOrchestrator(
                orderRepositoryPort,
                exchangeRateClientPort,
                walletClientPort,
                orderEventPublisherPort,
                orderNotificationPort
        );

        service = new CreateExchangeOrderService(sagaOrchestrator);
    }

    @Test
    @DisplayName("SAGA Exitoso: Bloqueo de saldo origen y validación de tasa -> Orden Creada")
    void shouldCreateExchangeOrderSuccessfullyThroughSaga() {
        when(exchangeRateClientPort.getExchangeRate(anyString(), anyString(), anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(BigDecimal.valueOf(3.7690));

        // Mock Paso 1: Bloqueo de fondos exitoso
        when(walletClientPort.lockFunds(any(LockFundsCommand.class)))
                .thenReturn(LockFundsResult.success("TRX-123", BigDecimal.valueOf(5000.00)));

        // Mock Paso 2: Validación de tasa exitosa
        when(exchangeRateClientPort.validateRate(any(ValidateRateQuery.class)))
                .thenReturn(ValidateRateResult.valid("TRX-123", BigDecimal.valueOf(3.7690)));

        when(orderRepositoryPort.save(any(ExchangeOrder.class)))
                .thenAnswer(i -> i.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                CurrencyType.USD,
                CurrencyType.PEN,
                OperationType.COMPRA,
                BigDecimal.valueOf(1000.00),
                0
        );

        CreateOrderResponse response = service.execute(request, "demo@cambistaonline.pe", "J");

        assertNotNull(response);
        assertNotNull(response.getOrderNumber());
        assertEquals("PENDING_PAYMENT", response.getStatus());
        assertEquals(0, BigDecimal.valueOf(3.7690).compareTo(response.getExchangeRate()));
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(response.getAmountSent()));
        assertEquals(0, BigDecimal.valueOf(3769.00).compareTo(response.getAmountReceived()));

        // Verificar que se ejecutó el bloqueo en wallet
        verify(walletClientPort, times(1)).lockFunds(any(LockFundsCommand.class));
        // Verificar que se validó la tasa
        verify(exchangeRateClientPort, times(1)).validateRate(any(ValidateRateQuery.class));
        // Verificar que no se activó ninguna compensación
        verify(walletClientPort, never()).unlockFunds(any(UnlockFundsCommand.class));
        // Verificar publicación en Kafka
        verify(orderEventPublisherPort, times(1)).publishOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("SAGA Compensación: Falla de tasa en Paso 2 activa UnlockFunds compensatorio")
    void shouldTriggerCompensationWhenRateValidationFails() {
        when(exchangeRateClientPort.getExchangeRate(anyString(), anyString(), anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(BigDecimal.valueOf(3.7690));

        // Mock Paso 1: Bloqueo exitoso
        when(walletClientPort.lockFunds(any(LockFundsCommand.class)))
                .thenReturn(LockFundsResult.success("TRX-123", BigDecimal.valueOf(5000.00)));

        // Mock Paso 2: Falla de tasa (expirada / mercado volátil)
        when(exchangeRateClientPort.validateRate(any(ValidateRateQuery.class)))
                .thenReturn(ValidateRateResult.invalid("TRX-123", BigDecimal.valueOf(3.8500), "Tasa cotizada ya no es válida"));

        // Mock compensación
        when(walletClientPort.unlockFunds(any(UnlockFundsCommand.class)))
                .thenReturn(UnlockFundsResult.success("TRX-123", BigDecimal.valueOf(6000.00)));

        CreateOrderRequest request = new CreateOrderRequest(
                CurrencyType.USD,
                CurrencyType.PEN,
                OperationType.COMPRA,
                BigDecimal.valueOf(1000.00),
                0
        );

        // Debe lanzar excepción al usuario
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.execute(request, "demo@cambistaonline.pe", "J"));

        assertTrue(ex.getMessage().contains("No se pudo garantizar la tasa"));

        // VERIFICACIÓN CLAVE DE LA TRANSACCIÓN COMPENSATORIA:
        // Se debió llamar obligatoriamente a unlockFunds en wallet-ledger-service
        verify(walletClientPort, times(1)).unlockFunds(any(UnlockFundsCommand.class));
        // No se debió guardar la orden ni publicar a Kafka
        verify(orderRepositoryPort, never()).save(any(ExchangeOrder.class));
        verify(orderEventPublisherPort, never()).publishOrderCreated(any(OrderCreatedEvent.class));
    }
}
