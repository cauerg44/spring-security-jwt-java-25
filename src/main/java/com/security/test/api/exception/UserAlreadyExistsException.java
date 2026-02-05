package com.security.test.api.exception;

@SuppressWarnings("serial")
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

