package com.cambistaonline.auth.application.mappers;

import com.cambistaonline.auth.application.dto.UserResponseDto;
import com.cambistaonline.auth.domain.model.User;

public class UserApplicationMapper {

    public static UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        return new UserResponseDto(
                user.getId(),
                user.getEmail() != null ? user.getEmail().getValue() : null,
                user.getFirstName(),
                user.getLastName(),
                user.getDni() != null ? user.getDni().getValue() : null,
                user.getCompanyName(),
                user.getRuc() != null ? user.getRuc().getValue() : null,
                user.getLegalRepresentativeName(),
                user.getRole(),
                user.getStatus() != null ? user.getStatus().name() : null,
                user.getCreatedAt(),
                user.isMfaEnabled(),
                user.getAuthProvider() != null ? user.getAuthProvider().name() : "LOCAL"
        );
    }
}
