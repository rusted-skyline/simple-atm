package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;
import com.bank909.atm.exception.InvalidAccountBalanceOperationException;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountService {

    Optional<BankAccount> findByAccountNumber(Long accountNumber);
    void deposit(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist, InvalidAccountBalanceOperationException;
    void withdraw(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist, InsufficientBalanceException;
}
