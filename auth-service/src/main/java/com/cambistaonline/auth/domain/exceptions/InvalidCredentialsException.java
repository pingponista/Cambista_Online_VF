package com.cambistaonline.auth.domain.exceptions;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
