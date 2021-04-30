package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountService {

    Optional<BankAccount> findById(Long id);
    Optional<BankAccount> findByAccountNumber(Long accountNumber);
    void deposit(Long id, BigDecimal amount) throws BankAccountDoesNotExist;
    void withdraw(Long id, BigDecimal amount) throws BankAccountDoesNotExist, InsufficientBalanceException;
}
