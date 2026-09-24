package com.cambistaonline.common.security;

/**
 * Constantes estandarizadas para propagación de contexto de seguridad y autenticación
 * en cabeceras HTTP, Metadata de gRPC y cabeceras de eventos Kafka.
 */
public final class SecurityContextMetadata {

    private SecurityContextMetadata() {}

    // Headers HTTP y gRPC Metadata Keys
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_MFA_VERIFIED = "X-MFA-Verified";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Kafka Record Header Keys
    public static final String KAFKA_HEADER_CORRELATION_ID = "correlation_id";
    public static final String KAFKA_HEADER_USER_EMAIL = "x_user_email";
    public static final String KAFKA_HEADER_USER_ROLE = "x_user_role";
    public static final String KAFKA_HEADER_MFA_VERIFIED = "x_mfa_verified";
    public static final String KAFKA_HEADER_TIMESTAMP = "x_event_timestamp";
}
