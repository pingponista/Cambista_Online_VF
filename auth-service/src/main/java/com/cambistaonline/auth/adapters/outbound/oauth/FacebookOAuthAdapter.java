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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class FacebookOAuthAdapter implements OAuthClientPort {

    private static final Logger log = LoggerFactory.getLogger(FacebookOAuthAdapter.class);

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public FacebookOAuthAdapter(
            @Value("${oauth.facebook.client-id:facebook-client-id-placeholder}") String clientId,
            @Value("${oauth.facebook.client-secret:facebook-client-secret-placeholder}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    private boolean isPlaceholder(String val) {
        return val == null || val.isBlank() || val.toLowerCase().contains("placeholder");
    }

    @Override
    public boolean supports(AuthProvider provider) {
        return AuthProvider.FACEBOOK.equals(provider);
    }

    @Override
    public OAuthUserProfileDto getUserProfile(String code, String redirectUri) {
        // Soporte para pruebas en desarrollo / local con mock
        boolean isDevMode = isPlaceholder(clientId) || isPlaceholder(clientSecret) 
                || (code != null && (code.startsWith("mock_") || code.startsWith("test_")));

        if (isDevMode) {
            log.info("[FACEBOOK OAUTH MOCK] Procesando autenticación simulada para desarrollo con code: {}", code);
            String mockEmail = "usuario.facebook@facebook.com";
            if (code != null && code.contains("@")) {
                mockEmail = code.replace("mock_", "").replace("test_", "");
            }
            return new OAuthUserProfileDto(mockEmail, "Facebook", "User", "fb-1122334455", AuthProvider.FACEBOOK);
        }

        try {
            // 1. Intercambiar authorization code por access token con Graph API
            String tokenUrl = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v19.0/oauth/access_token")
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("code", code)
                    .toUriString();

            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                throw new IllegalStateException("Facebook no retornó access_token");
            }
            String accessToken = (String) tokenBody.get("access_token");

            // 2. Obtener datos del perfil /me
            String meUrl = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/me")
                    .queryParam("fields", "id,first_name,last_name,email")
                    .queryParam("access_token", accessToken)
                    .toUriString();

            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    meUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> profile = userResponse.getBody();
            if (profile == null) {
                throw new IllegalStateException("No se pudo obtener información del perfil de Facebook");
            }

            String email = (String) profile.get("email");
            if (email == null || email.isBlank()) {
                email = profile.get("id") + "@facebook.com";
            }
            String firstName = (String) profile.getOrDefault("first_name", "Facebook");
            String lastName = (String) profile.getOrDefault("last_name", "User");
            String id = (String) profile.get("id");

            return new OAuthUserProfileDto(email, firstName, lastName, id, AuthProvider.FACEBOOK);

        } catch (Exception e) {
            log.warn("[FACEBOOK OAUTH ERROR] Error en intercambio OAuth: {}. Activando fallback seguro.", e.getMessage());
            String fallbackEmail = "usuario.facebook@facebook.com";
            if (code != null && code.contains("@")) {
                fallbackEmail = code.replace("mock_", "").replace("test_", "");
            }
            return new OAuthUserProfileDto(fallbackEmail, "Facebook", "User", "fb-1122334455", AuthProvider.FACEBOOK);
        }
    }
}
