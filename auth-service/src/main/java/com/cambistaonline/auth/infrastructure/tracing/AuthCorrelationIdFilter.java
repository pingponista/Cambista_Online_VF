package com.cambistaonline.auth.infrastructure.tracing;

import com.cambistaonline.common.security.SecurityContextMetadata;
import com.cambistaonline.common.tracing.CorrelationContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro web para auth-service que captura o genera el Correlation ID,
 * sincronizandolo con el MDC de SLF4J para trazabilidad distribuida.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthCorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String incomingCid = httpRequest.getHeader(SecurityContextMetadata.HEADER_CORRELATION_ID);
            String userEmail = httpRequest.getHeader(SecurityContextMetadata.HEADER_USER_EMAIL);
            String mfaHeader = httpRequest.getHeader(SecurityContextMetadata.HEADER_MFA_VERIFIED);
            boolean mfaVerified = "true".equalsIgnoreCase(mfaHeader);

            String correlationId = CorrelationContext.getOrCreateCorrelationId(incomingCid);
            CorrelationContext.setContext(correlationId, userEmail, mfaVerified);

            httpResponse.setHeader(SecurityContextMetadata.HEADER_CORRELATION_ID, correlationId);
        }

 try {
 chain.doFilter(request, response);
 } finally {
 CorrelationContext.clearContext();
 }
 }
}