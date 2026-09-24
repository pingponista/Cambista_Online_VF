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

import java.util.List;
import java.util.Map;

@Component
public class GitHubOAuthAdapter implements OAuthClientPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubOAuthAdapter.class);

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public GitHubOAuthAdapter(
            @Value("${oauth.github.client-id:github-client-id-placeholder}") String clientId,
            @Value("${oauth.github.client-secret:github-client-secret-placeholder}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean supports(AuthProvider provider) {
        return AuthProvider.GITHUB.equals(provider);
    }

    @Override
    public OAuthUserProfileDto getUserProfile(String code, String redirectUri) {
        // Soporte para pruebas en desarrollo / local con mock
        if (code != null && (code.startsWith("mock_") || code.startsWith("test_") || "github-client-id-placeholder".equals(clientId))) {
            log.info("[GITHUB OAUTH MOCK] Procesando autenticación simulada para desarrollo con code: {}", code);
            String mockEmail = "developer.github@github.com";
            if (code.contains("@")) {
                mockEmail = code.replace("mock_", "").replace("test_", "");
            }
            return new OAuthUserProfileDto(mockEmail, "GitHub", "Dev", "gh-987654321", AuthProvider.GITHUB);
        }

        try {
            // 1. Intercambiar code por token en GitHub
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.USER_AGENT, "CambistaOnline-App");

            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("client_id", clientId);
            body.put("client_secret", clientSecret);
            body.put("code", code);
            if (redirectUri != null && !redirectUri.isBlank()) {
                body.put("redirect_uri", redirectUri);
            }

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    "https://github.com/login/oauth/access_token",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                throw new IllegalStateException("GitHub no retornó access_token");
            }
            String accessToken = (String) tokenBody.get("access_token");

            // 2. Consultar perfil en /user
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            authHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            authHeaders.set(HttpHeaders.USER_AGENT, "CambistaOnline-App");
            HttpEntity<Void> userRequest = new HttpEntity<>(authHeaders);

            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    userRequest,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> profile = userResponse.getBody();
            if (profile == null) {
                throw new IllegalStateException("No se pudo obtener información del perfil de GitHub");
            }

            String email = (String) profile.get("email");
            if (email == null) {
                // Si el email es privado, consultar /user/emails
                try {
                    ResponseEntity<List<Map<String, Object>>> emailsResponse = restTemplate.exchange(
                            "https://api.github.com/user/emails",
                            HttpMethod.GET,
                            userRequest,
                            new ParameterizedTypeReference<>() {}
                    );
                    List<Map<String, Object>> emails = emailsResponse.getBody();
                    if (emails != null) {
                        for (Map<String, Object> emailObj : emails) {
                            Boolean primary = (Boolean) emailObj.get("primary");
                            if (Boolean.TRUE.equals(primary)) {
                                email = (String) emailObj.get("email");
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("No se pudieron obtener los correos secundarios de GitHub: {}", e.getMessage());
                }
            }

            if (email == null) {
                email = profile.get("login") + "@github.com";
            }

            String name = (String) profile.getOrDefault("name", profile.get("login"));
            String id = String.valueOf(profile.get("id"));

            return new OAuthUserProfileDto(email, name, "GitHub", id, AuthProvider.GITHUB);

        } catch (Exception e) {
            log.error("[GITHUB OAUTH ERROR] Error en intercambio OAuth: {}", e.getMessage());
            throw new RuntimeException("Error al autenticar con GitHub: " + e.getMessage(), e);
        }
    }
}
