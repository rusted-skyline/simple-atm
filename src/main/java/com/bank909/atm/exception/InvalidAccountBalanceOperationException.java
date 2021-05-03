package com.bank909.atm.exception;

/**
 * Thrown when an invalid operation was attempted for a bank account
 */
public class InvalidAccountBalanceOperationException extends Throwable {

    public InvalidAccountBalanceOperationException(String message) {
        super(message);
    }
}
