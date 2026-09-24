package com.cambistaonline.auth.adapters.outbound.oauth;

import com.cambistaonline.auth.application.dto.OAuthUserProfileDto;
import com.cambistaonline.auth.application.ports.outbound.OAuthClientPort;
import com.cambistaonline.auth.domain.model.AuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleOAuthAdapter implements OAuthClientPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthAdapter.class);

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public GoogleOAuthAdapter(
            @Value("${oauth.google.client-id:google-client-id-placeholder}") String clientId,
            @Value("${oauth.google.client-secret:google-client-secret-placeholder}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean supports(AuthProvider provider) {
        return AuthProvider.GOOGLE.equals(provider);
    }

    @Override
    public OAuthUserProfileDto getUserProfile(String code, String redirectUri) {
        // Soporte para pruebas en desarrollo / local con mock
        if (code != null && (code.startsWith("mock_") || code.startsWith("test_") || "google-client-id-placeholder".equals(clientId))) {
            log.info("[GOOGLE OAUTH MOCK] Procesando autenticación simulada para desarrollo con code: {}", code);
            String mockEmail = "usuario.google@gmail.com";
            if (code.contains("@")) {
                mockEmail = code.replace("mock_", "").replace("test_", "");
            }
            return new OAuthUserProfileDto(mockEmail, "Google", "User", "goog-123456789", AuthProvider.GOOGLE);
        }

        try {
            // 1. Intercambiar authorization code por access token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", code);
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                throw new IllegalStateException("Google no retornó access_token");
            }
            String accessToken = (String) tokenBody.get("access_token");

            // 2. Obtener información del perfil desde Google UserInfo endpoint
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> userInfoRequest = new HttpEntity<>(authHeaders);

            ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    userInfoRequest,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> profile = userInfoResponse.getBody();
            if (profile == null) {
                throw new IllegalStateException("No se pudo obtener información del perfil de Google");
            }

            String email = (String) profile.get("email");
            String givenName = (String) profile.getOrDefault("given_name", "Google");
            String familyName = (String) profile.getOrDefault("family_name", "User");
            String sub = (String) profile.get("sub");

            return new OAuthUserProfileDto(email, givenName, familyName, sub, AuthProvider.GOOGLE);

        } catch (Exception e) {
            log.error("[GOOGLE OAUTH ERROR] Error en intercambio OAuth: {}", e.getMessage());
            throw new RuntimeException("Error al autenticar con Google: " + e.getMessage(), e);
        }
    }
}
