package com.cambistaonline.auth.infrastructure.rest;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.OAuthAuthCommand;
import com.cambistaonline.auth.application.ports.inbound.AuthenticateOAuthUserUseCase;
import com.cambistaonline.auth.domain.model.AuthProvider;
import com.cambistaonline.auth.infrastructure.rest.request.OAuthLoginRequest;
import com.cambistaonline.auth.infrastructure.rest.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/oauth")
@Tag(name = "Authentication - OAuth", description = "Endpoints para autenticación social con Google, GitHub y Facebook")
public class OAuthController {

    private final AuthenticateOAuthUserUseCase authenticateOAuthUserUseCase;

    public OAuthController(AuthenticateOAuthUserUseCase authenticateOAuthUserUseCase) {
        this.authenticateOAuthUserUseCase = authenticateOAuthUserUseCase;
    }

    @PostMapping("/{provider}")
    @Operation(summary = "Autenticar mediante proveedor social (google, github, facebook)")
    public ResponseEntity<LoginResponse> authenticateSocial(
            @PathVariable("provider") String providerStr,
            @Valid @RequestBody OAuthLoginRequest request
    ) {
        AuthProvider provider = AuthProvider.fromString(providerStr);
        OAuthAuthCommand command = new OAuthAuthCommand(provider, request.getCode(), request.getRedirectUri());

        AuthTokenResponseDto tokenDto = authenticateOAuthUserUseCase.execute(command);

        if (tokenDto.isMfaRequired()) {
            return ResponseEntity.ok(LoginResponse.mfaRequired(tokenDto.getMfaSessionToken()));
        }

        LoginResponse response = new LoginResponse(
                tokenDto.getAccessToken(),
                tokenDto.getTokenType(),
                tokenDto.getExpiresIn()
        );

        return ResponseEntity.ok(response);
    }
}
