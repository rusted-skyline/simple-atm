package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InvalidAccountBalanceOperationException;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountService {

    /**
     * Find a single bank account by accountNumber.
     *
     * @param accountNumber
     * @return a bank account
     */
    Optional<BankAccount> findByAccountNumber(Long accountNumber);

    /**
     * Deposit an amount into accountNumber
     *
     * @param accountNumber bank account number
     * @param amount        amount to deposit
     * @throws BankAccountDoesNotExist if the bank account is not found
     * @throws InvalidAccountBalanceOperationException if an invalid deposit is attempted
     */
    void deposit(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist,
            InvalidAccountBalanceOperationException;

    /**
     * Withdraw an amount from accountNumber
     *
     * @param accountNumber bank account number
     * @param amount        amount to withdraw
     * @throws BankAccountDoesNotExist if the bank account is not found
     * @throws InvalidAccountBalanceOperationException if an invalid withdraw is attempted
     */
    void withdraw(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist,
            InvalidAccountBalanceOperationException;
}
