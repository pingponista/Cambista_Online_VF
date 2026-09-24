package com.cambistaonline.auth.infrastructure.rest;

import com.cambistaonline.auth.application.dto.AuthenticateUserCommand;
import com.cambistaonline.auth.application.dto.AuthTokenResponseDto;
import com.cambistaonline.auth.application.dto.RegisterUserCommand;
import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.application.ports.inbound.AuthenticateUserUseCase;
import com.cambistaonline.auth.application.ports.inbound.GetCurrentUserUseCase;
import com.cambistaonline.auth.application.ports.inbound.RegisterUserUseCase;
import com.cambistaonline.auth.infrastructure.rest.request.LoginRequest;
import com.cambistaonline.auth.infrastructure.rest.request.RegisterRequest;
import com.cambistaonline.auth.infrastructure.rest.response.LoginResponse;
import com.cambistaonline.auth.infrastructure.rest.response.UserProfileResponse;
import com.cambistaonline.auth.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints empresariales de autenticación, registro y gestión de usuarios")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, AuthenticateUserUseCase authenticateUserUseCase, GetCurrentUserUseCase getCurrentUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (Persona Natural / Persona Jurídica)")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getDni(),
                request.getCompanyName(),
                request.getRuc(),
                request.getLegalRepresentativeName(),
                request.getRole()
        );

        UserResponseDto userResponse = registerUserUseCase.execute(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Se registro exitosamente", userResponse));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario y obtener JWT Access Token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateUserCommand command = new AuthenticateUserCommand(
                request.getEmail(),
                request.getPassword()
        );

        AuthTokenResponseDto tokenDto = authenticateUserUseCase.execute(command);
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

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado mediante JWT", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponseDto userDto = getCurrentUserUseCase.execute(email);

        UserProfileResponse response = new UserProfileResponse(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getDni(),
                userDto.getCompanyName(),
                userDto.getRuc(),
                userDto.getLegalRepresentativeName(),
                userDto.getRole(),
                userDto.getStatus(),
                userDto.getCreatedAt(),
                userDto.isMfaEnabled(),
                userDto.getAuthProvider()
        );

        return ResponseEntity.ok(ApiResponse.success("Perfil obtenido correctamente", response));
    }
}
