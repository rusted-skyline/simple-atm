package com.bank909.atm.exception;

public class BankAccountDoesNotExist extends Throwable {

    public BankAccountDoesNotExist(String message) {
        super(message);
    }
}
