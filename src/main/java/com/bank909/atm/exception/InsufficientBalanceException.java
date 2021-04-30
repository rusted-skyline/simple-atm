package com.bank909.atm.exception;

public class InsufficientBalanceException extends Throwable {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
