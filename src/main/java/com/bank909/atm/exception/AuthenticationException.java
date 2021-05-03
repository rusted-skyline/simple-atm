package com.bank909.atm.exception;

/**
 * Thrown when an authentication error has occurred
 */
public class AuthenticationException extends Throwable {

    public AuthenticationException(String message) {
        super(message);
    }
}
