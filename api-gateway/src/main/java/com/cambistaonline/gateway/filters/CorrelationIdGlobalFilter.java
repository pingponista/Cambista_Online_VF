package com.cambistaonline.gateway.filters;

import com.cambistaonline.common.security.SecurityContextMetadata;
import com.cambistaonline.common.tracing.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtro Global en el API Gateway que intercepta cada petición perimetral entrante:
 * 1. Extrae o genera el Correlation ID (Trace ID).
 * 2. Inyecta el Correlation ID en los headers downstream hacia los microservicios.
 * 3. Devuelve el Correlation ID en la respuesta HTTP hacia el cliente para trazabilidad y soporte técnico.
 */
@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        List<String> incomingCids = request.getHeaders().get(SecurityContextMetadata.HEADER_CORRELATION_ID);
        String incomingCid = (incomingCids != null && !incomingCids.isEmpty()) ? incomingCids.get(0) : null;

        String correlationId = CorrelationContext.getOrCreateCorrelationId(incomingCid);

        log.info("[GATEWAY] 🌐 Enrutando {} {} | CorrelationId={}",
                request.getMethod(), request.getURI().getPath(), correlationId);

        // Mutar la petición agregando la cabecera X-Correlation-Id downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(SecurityContextMetadata.HEADER_CORRELATION_ID, correlationId)
                .build();

        // Mutar la respuesta HTTP para devolver el header al cliente exterior
        exchange.getResponse().getHeaders().add(SecurityContextMetadata.HEADER_CORRELATION_ID, correlationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
