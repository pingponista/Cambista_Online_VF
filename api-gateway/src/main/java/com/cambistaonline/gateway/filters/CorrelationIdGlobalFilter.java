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
 * CorrelationIdGlobalFilter: Filtro reactivo perimetral en Spring Cloud Gateway.
 * Intercepta ABSOLUTAMENTE TODAS las llamadas HTTP que llegan desde el navegador o app móvil.
 *
 * ¿QUÉ ES UN FILTRO REACTIVO EN SPRING?
 * Spring Cloud Gateway no usa el modelo clásico de un hilo por petición (Tomcat), sino
 * programación asíncrona reactiva sin bloqueo (Netty + Project Reactor).
 * - 'ServerWebExchange': Representa la petición HTTP y la respuesta en curso.
 * - 'GatewayFilterChain': Es la cadena de filtros por la que debe pasar la petición.
 * - 'Mono<Void>': Es una promesa/futuro asíncrono que avisa cuando la operación terminó sin bloquear la CPU.
 *
 * ¿PARA QUÉ SIRVE EL CORRELATION ID?
 * En un sistema de microservicios con 4 servicios, cuando algo falla es imposible saber qué pasó
 * si cada servicio tiene logs aislados. El Correlation ID es un código único (ej: "cid-a1b2c3d4...")
 * que viaja en la cabecera HTTP a través de todos los microservicios y se imprime en cada línea de log.
 * Así, buscando ese ID en los logs, puedes ver la película completa de la petición.
 */
@Component // Marca esta clase para que Spring la cree e inyecte automáticamente como un Bean singleton
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    // Logger para imprimir mensajes formateados en la terminal/consola
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
