package com.wtc.auth;

/**
 * Thrown when an account is registered with an e-mail that already exists.
 * Mapped to HTTP 409 (Conflict) by the global exception handler.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
