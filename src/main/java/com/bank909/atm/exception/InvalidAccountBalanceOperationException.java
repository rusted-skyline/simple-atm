package com.bank909.atm.exception;

public class InvalidAccountBalanceOperationException extends Throwable {
    public InvalidAccountBalanceOperationException(String message) {
        super(message);
    }
}
