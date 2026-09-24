package com.cambistaonline.order.application;

import com.cambistaonline.order.adapters.outbound.client.HttpExchangeRateClientAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExchangeRateResilienceFallbackTest {

    @Test
    @DisplayName("Circuit Breaker Fallback: Sirve tasa de contingencia ante caída de exchange-rate-service")
    void shouldReturnContingencyRateOnCircuitBreakerFallback() {
        HttpExchangeRateClientAdapter adapter = new HttpExchangeRateClientAdapter(new RestTemplateBuilder());

        // Invocar el método de fallback directamente como lo hace el interceptor de Resilience4j
        BigDecimal fallbackRate = adapter.getExchangeRateFallback(
                "USD",
                "PEN",
                "COMPRA",
                0,
                "demo@cambistaonline.pe",
                "J",
                new RuntimeException("Simulated connection timeout to exchange-rate-service")
        );

        assertNotNull(fallbackRate);
        assertEquals(0, BigDecimal.valueOf(3.7565).compareTo(fallbackRate));
    }
}
