package com.cambistaonline.auth.domain.exceptions;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
