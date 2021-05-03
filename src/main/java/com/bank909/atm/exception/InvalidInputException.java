package com.bank909.atm.exception;

/**
 * Thrown when invalid input values are entered by user
 */
public class InvalidInputException extends Throwable {

    public InvalidInputException(String message) {
        super(message);
    }
}
