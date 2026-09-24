package com.cambistaonline.auth.infrastructure.rest;

import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.DisableMfaCommand;
import com.cambistaonline.auth.application.dto.EnableMfaCommand;
import com.cambistaonline.auth.application.dto.SetupMfaResponseDto;
import com.cambistaonline.auth.application.dto.VerifyMfaCommand;
import com.cambistaonline.auth.application.ports.inbound.DisableMfaUseCase;
import com.cambistaonline.auth.application.ports.inbound.EnableMfaUseCase;
import com.cambistaonline.auth.application.ports.inbound.SetupMfaUseCase;
import com.cambistaonline.auth.application.ports.inbound.VerifyMfaUseCase;
import com.cambistaonline.auth.infrastructure.rest.request.MfaCodeRequest;
import com.cambistaonline.auth.infrastructure.rest.request.MfaVerifyRequest;
import com.cambistaonline.auth.infrastructure.rest.response.LoginResponse;
import com.cambistaonline.auth.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/mfa")
@Tag(name = "Authentication - MFA", description = "Endpoints para doble factor de autenticación TOTP (Google Authenticator)")
public class MfaController {

    private final SetupMfaUseCase setupMfaUseCase;
    private final EnableMfaUseCase enableMfaUseCase;
    private final VerifyMfaUseCase verifyMfaUseCase;
    private final DisableMfaUseCase disableMfaUseCase;

    public MfaController(SetupMfaUseCase setupMfaUseCase,
                         EnableMfaUseCase enableMfaUseCase,
                         VerifyMfaUseCase verifyMfaUseCase,
                         DisableMfaUseCase disableMfaUseCase) {
        this.setupMfaUseCase = setupMfaUseCase;
        this.enableMfaUseCase = enableMfaUseCase;
        this.verifyMfaUseCase = verifyMfaUseCase;
        this.disableMfaUseCase = disableMfaUseCase;
    }

    @PostMapping("/setup")
    @Operation(summary = "Generar secreto TOTP y URL para código QR (Google Authenticator)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<SetupMfaResponseDto>> setupMfa(Authentication authentication) {
        String email = authentication.getName();
        SetupMfaResponseDto dto = setupMfaUseCase.execute(email);
        return ResponseEntity.ok(ApiResponse.success("Secreto MFA generado exitosamente", dto));
    }

    @PostMapping("/enable")
    @Operation(summary = "Confirmar código TOTP inicial y activar MFA para el usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Boolean>> enableMfa(
            Authentication authentication,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        String email = authentication.getName();
        boolean enabled = enableMfaUseCase.execute(new EnableMfaCommand(email, request.getCode()));
        return ResponseEntity.ok(ApiResponse.success("MFA activado exitosamente en tu cuenta", enabled));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verificar código TOTP de 6 dígitos durante el login y obtener JWT final")
    public ResponseEntity<LoginResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        VerifyMfaCommand command = new VerifyMfaCommand(request.getMfaSessionToken(), request.getCode());
        AuthTokenResponseDto tokenDto = verifyMfaUseCase.execute(command);

        LoginResponse response = new LoginResponse(
                tokenDto.getAccessToken(),
                tokenDto.getTokenType(),
                tokenDto.getExpiresIn()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/disable")
    @Operation(summary = "Desactivar MFA ingresando el código TOTP actual", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Boolean>> disableMfa(
            Authentication authentication,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        String email = authentication.getName();
        boolean disabled = disableMfaUseCase.execute(new DisableMfaCommand(email, request.getCode()));
        return ResponseEntity.ok(ApiResponse.success("MFA desactivado correctamente", disabled));
    }
}
