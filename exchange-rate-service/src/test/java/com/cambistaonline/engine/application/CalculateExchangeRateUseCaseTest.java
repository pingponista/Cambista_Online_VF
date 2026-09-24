package com.cambistaonline.engine.application;

import com.cambistaonline.engine.application.dto.CalculateRateRequest;
import com.cambistaonline.engine.application.dto.CalculateRateResponse;
import com.cambistaonline.engine.application.usecases.CalculateExchangeRateUseCase;
import com.cambistaonline.engine.domain.model.CurrencyType;
import com.cambistaonline.engine.domain.model.CustomerLevel;
import com.cambistaonline.engine.domain.model.OperationType;
import com.cambistaonline.engine.domain.ports.ExchangeRateRepositoryPort;
import com.cambistaonline.engine.domain.ports.ExchangeRuleRepositoryPort;
import com.cambistaonline.engine.domain.ports.UserPointsRepositoryPort;
import com.cambistaonline.engine.domain.strategy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class CalculateExchangeRateUseCaseTest {

    private ExchangeRateRepositoryPort rateRepositoryPort;
    private ExchangeRuleRepositoryPort ruleRepositoryPort;
    private UserPointsRepositoryPort pointsRepositoryPort;
    private CalculateExchangeRateUseCase useCase;

    @BeforeEach
    void setUp() {
        rateRepositoryPort = Mockito.mock(ExchangeRateRepositoryPort.class);
        ruleRepositoryPort = Mockito.mock(ExchangeRuleRepositoryPort.class);
        pointsRepositoryPort = Mockito.mock(UserPointsRepositoryPort.class);

        SbsBaseRateStrategy sbsStrategy = new SbsBaseRateStrategy(rateRepositoryPort);
        CustomerSpreadStrategy spreadStrategy = new CustomerSpreadStrategy(ruleRepositoryPort);
        HourlyAdjustmentStrategy hourlyStrategy = new HourlyAdjustmentStrategy(ruleRepositoryPort);
        SeasonalAdjustmentStrategy seasonalStrategy = new SeasonalAdjustmentStrategy(ruleRepositoryPort);
        PointsRedemptionStrategy pointsStrategy = new PointsRedemptionStrategy(pointsRepositoryPort);

        ExchangeRateCalculationPipeline pipeline = new ExchangeRateCalculationPipeline(List.of(
                sbsStrategy, spreadStrategy, hourlyStrategy, seasonalStrategy, pointsStrategy
        ));

        useCase = new CalculateExchangeRateUseCase(pipeline);
    }

    @Test
    @DisplayName("Debe calcular el tipo de cambio dinámico correctamente para un usuario con rol 'J'")
    void shouldCalculateExchangeRateSuccessfullyForRoleJ() {
        // Arrange
        when(rateRepositoryPort.findActiveBaseRate(eq(CurrencyType.USD), eq(CurrencyType.PEN), eq(OperationType.COMPRA)))
                .thenReturn(Optional.of(BigDecimal.valueOf(3.7565)));

        when(ruleRepositoryPort.findSpread(eq("J"), eq(CustomerLevel.PREFERENTE), eq(OperationType.COMPRA)))
                .thenReturn(Optional.of(BigDecimal.valueOf(0.0100)));

        when(ruleRepositoryPort.findHourlyAdjustment(eq("J"), any(LocalTime.class), eq(OperationType.COMPRA)))
                .thenReturn(Optional.of(BigDecimal.valueOf(0.0050)));

        when(ruleRepositoryPort.findSeasonalAdjustment(eq("J"), any(LocalDateTime.class), eq(OperationType.COMPRA)))
                .thenReturn(Optional.of(BigDecimal.valueOf(-0.0010)));

        when(pointsRepositoryPort.getUserPointsBalance(eq("juan@empresa.com")))
                .thenReturn(320);

        when(pointsRepositoryPort.findPointsConfig(eq("J")))
                .thenReturn(Optional.of(new UserPointsRepositoryPort.PointsConfig(100, 100, BigDecimal.valueOf(0.0005))));

        CalculateRateRequest request = new CalculateRateRequest(CurrencyType.USD, CurrencyType.PEN, OperationType.COMPRA, 100);

        // Act
        CalculateRateResponse response = useCase.execute(request, "juan@empresa.com", "Juan Pérez", "J", CustomerLevel.PREFERENTE);

        // Assert
        assertNotNull(response);
        assertEquals("00", response.getCodigo());
        assertEquals("J", response.getUsuario().getRol());
        assertEquals("juan@empresa.com", response.getUsuario().getCorreo());

        // Base 3.7565 + Spread 0.0100 + Hourly 0.0050 - Seasonal 0.0010 - Points 0.0005 = 3.7700
        assertEquals(0, new BigDecimal("3.7565").compareTo(response.getTipoCambio().getBaseSbs()));
        assertEquals(0, new BigDecimal("0.0100").compareTo(response.getTipoCambio().getSpread()));
        assertEquals(0, new BigDecimal("0.0050").compareTo(response.getTipoCambio().getAjusteHorario()));
        assertEquals(0, new BigDecimal("-0.0010").compareTo(response.getTipoCambio().getAjusteEstacional()));
        assertEquals(0, new BigDecimal("-0.0005").compareTo(response.getTipoCambio().getCanjePuntos()));
        assertEquals(0, new BigDecimal("3.7700").compareTo(response.getTipoCambio().getTipoCambioFinal()));


        assertEquals(320, response.getBeneficios().getSaldoPuntos());
        assertEquals(100, response.getBeneficios().getPuntosCanjeados());
        assertEquals(5, response.getDetalle().size());
    }

    @Test
    @DisplayName("Debe rechazar solicitudes con roles no permitidos (diferentes de 'J' o 'N')")
    void shouldRejectInvalidUserRoles() {
        CalculateRateRequest request = new CalculateRateRequest();
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(request, "user@test.com", "Test User", "GUEST", CustomerLevel.NORMAL)
        );
    }
}
