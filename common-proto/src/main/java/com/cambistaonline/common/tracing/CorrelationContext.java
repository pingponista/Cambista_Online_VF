package com.cambistaonline.common.tracing;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Gestor de contexto de trazabilidad distribuida (Correlation ID / Trace ID).
 * Mantiene la correlación en ThreadLocal y sincroniza con el MDC (Mapped Diagnostic Context)
 * de SLF4J para que todos los logs contengan automáticamente el ID de correlación y usuario.
 */
public final class CorrelationContext {

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_USER_EMAIL = "userEmail";
    public static final String MDC_MFA_VERIFIED = "mfaVerified";

    private CorrelationContext() {}

    public static String getOrCreateCorrelationId(String existingId) {
        if (existingId != null && !existingId.trim().isEmpty()) {
            return existingId;
        }
        return "cid-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static void setContext(String correlationId, String userEmail, boolean mfaVerified) {
        String cid = getOrCreateCorrelationId(correlationId);
        MDC.put(MDC_CORRELATION_ID, cid);
        if (userEmail != null) {
            MDC.put(MDC_USER_EMAIL, userEmail);
        }
        MDC.put(MDC_MFA_VERIFIED, String.valueOf(mfaVerified));
    }

    public static String getCorrelationId() {
        String cid = MDC.get(MDC_CORRELATION_ID);
        return cid != null ? cid : getOrCreateCorrelationId(null);
    }

    public static String getUserEmail() {
        return MDC.get(MDC_USER_EMAIL);
    }

    public static boolean isMfaVerified() {
        return Boolean.parseBoolean(MDC.get(MDC_MFA_VERIFIED));
    }

    public static void clearContext() {
        MDC.remove(MDC_CORRELATION_ID);
        MDC.remove(MDC_USER_EMAIL);
        MDC.remove(MDC_MFA_VERIFIED);
    }
}
