package com.cambistaonline.auth.application.ports.outbound;

import com.cambistaonline.auth.application.dto.OAuthUserProfileDto;
import com.cambistaonline.auth.domain.model.AuthProvider;

public interface OAuthClientPort {
    boolean supports(AuthProvider provider);
    OAuthUserProfileDto getUserProfile(String code, String redirectUri);
}
