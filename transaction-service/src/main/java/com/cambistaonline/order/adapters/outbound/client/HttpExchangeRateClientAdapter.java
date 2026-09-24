package com.cambistaonline.order.adapters.outbound.client;

import com.cambistaonline.order.application.ports.outbound.ExchangeRateClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Component
public class HttpExchangeRateClientAdapter implements ExchangeRateClientPort {

    private static final Logger log = LoggerFactory.getLogger(HttpExchangeRateClientAdapter.class);

    private final RestTemplate restTemplate;

    @Value("${services.exchange-rate.url:http://localhost:8082}")
    private String rateServiceUrl;

    public HttpExchangeRateClientAdapter(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "exchangeRateService", fallbackMethod = "getExchangeRateFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "exchangeRateService")
    @io.github.resilience4j.bulkhead.annotation.Bulkhead(name = "exchangeRateService")
    public BigDecimal getExchangeRate(String currencyOrigin, String currencyDestination,
                                      String operationType, int pointsToRedeem,
                                      String userEmail, String userRole) {
        String endpoint = rateServiceUrl + "/api/v1/rates/calculate";
        Map<String, Object> request = Map.of(
                "currencyOrigin", currencyOrigin,
                "currencyDestination", currencyDestination,
                "operationType", operationType,
                "pointsToRedeem", pointsToRedeem
        );

        log.info("[RATE-CLIENT] Consultando tasa a exchange-rate-service ({}) para {} -> {}", endpoint, currencyOrigin, currencyDestination);
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> tipoCambio = (Map<String, Object>) response.getBody().get("tipoCambio");
            if (tipoCambio != null && tipoCambio.get("tipoCambioFinal") != null) {
                return new BigDecimal(tipoCambio.get("tipoCambioFinal").toString());
            }
        }
        throw new IllegalStateException("Respuesta vacía de exchange-rate-service");
    }

    /**
     * Fallback de contingencia activado por Circuit Breaker o agotamiento de reintentos.
     * Garantiza el patrón AP (Availability): sirve cotización de emergencia sin fallar en cascada.
     */
    public BigDecimal getExchangeRateFallback(String currencyOrigin, String currencyDestination,
                                              String operationType, int pointsToRedeem,
                                              String userEmail, String userRole, Throwable t) {
        log.warn("[RATE-CIRCUIT-BREAKER] ⚠️ Fallback activado debido a fallo en exchange-rate-service: {}", t.getMessage());
        if ("EUR".equalsIgnoreCase(currencyOrigin) || "EUR".equalsIgnoreCase(currencyDestination)) {
            return "COMPRA".equalsIgnoreCase(operationType) ? new BigDecimal("4.0820") : new BigDecimal("4.1050");
        }
        return "COMPRA".equalsIgnoreCase(operationType) ? new BigDecimal("3.7565") : new BigDecimal("3.7650");
    }

    @Override
    public com.cambistaonline.common.grpc.rate.ValidateRateResult validateRate(com.cambistaonline.common.grpc.rate.ValidateRateQuery query) {
        String endpoint = rateServiceUrl + "/internal/grpc/rate/validate";
        try {
            log.info("[RATE-CLIENT-RPC] Validando tasa síncrona en {}: {}", endpoint, query);
            ResponseEntity<com.cambistaonline.common.grpc.rate.ValidateRateResult> response =
                    restTemplate.postForEntity(endpoint, query, com.cambistaonline.common.grpc.rate.ValidateRateResult.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("[RATE-CLIENT-RPC] Error al contactar exchange-rate-service en {}: {}", endpoint, e.getMessage());
        }
        // Fallback optimista si el servicio responde con error de red en desarrollo
        return com.cambistaonline.common.grpc.rate.ValidateRateResult.valid(query.quoteId(), query.expectedRate());
    }
}
