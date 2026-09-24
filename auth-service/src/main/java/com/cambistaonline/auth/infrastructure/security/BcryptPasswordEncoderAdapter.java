package com.cambistaonline.auth.infrastructure.security;

import com.cambistaonline.auth.domain.ports.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort, com.cambistaonline.auth.application.ports.outbound.PasswordEncoderPort {

    private final PasswordEncoder springEncoder;

    public BcryptPasswordEncoderAdapter() {
        this.springEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return springEncoder.encode(rawPassword);
    }

    @Override
    public String encode(String rawPassword) {
        return springEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return springEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return springEncoder.matches(rawPassword, encodedPassword);
    }
}
