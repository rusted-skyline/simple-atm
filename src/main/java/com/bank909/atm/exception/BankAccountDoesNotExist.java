package com.bank909.atm.exception;

/**
 * Thrown when a bank account could not be found
 */
public class BankAccountDoesNotExist extends Throwable {

    public BankAccountDoesNotExist(String message) {
        super(message);
    }
}
