package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InvalidAccountBalanceOperationException;
import com.bank909.atm.repository.BankAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BankAccountServiceImplTests {

    private BankAccountRepository bankAccountRepository;

    private BankAccount testBankAccount;
    private Optional<BankAccount> testBankAccountOptional;

    @BeforeEach
    void setup() {
        bankAccountRepository = mock(BankAccountRepository.class);

        OffsetDateTime created = OffsetDateTime.now();

        testBankAccount = new BankAccount();
        testBankAccount.setBalance(new BigDecimal(0));
        testBankAccount.setAccountNumber(new Long("1111222233334444"));
        testBankAccount.setAccountNumber(new Long(1));
        testBankAccount.setCreated(created);
        testBankAccount.setUpdated(created);
        testBankAccountOptional = Optional.of(testBankAccount);
    }

    @AfterEach
    void teardown() {
        testBankAccount = null;
        testBankAccountOptional = null;
    }

    @Test
    void testDeposit() throws BankAccountDoesNotExist, InterruptedException, InvalidAccountBalanceOperationException {
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
        when(bankAccountRepository.findByAccountNumber(testBankAccount.getAccountNumber()))
                .thenReturn(testBankAccountOptional);

        // ensure created and updated times can be different
        Thread.sleep(10);
        BigDecimal depositAmount = new BigDecimal("10.00");
        bankAccountService.deposit(testBankAccount.getAccountNumber(), depositAmount);
        assertEquals(testBankAccountOptional.get().getBalance(), depositAmount);
        assertNotEquals(testBankAccountOptional.get().getUpdated(), testBankAccountOptional.get().getCreated());
    }

    @Test
    void testDepositExceedMaxBalance() {
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
        when(bankAccountRepository.findByAccountNumber(testBankAccount.getAccountNumber()))
                .thenReturn(testBankAccountOptional);

        BigDecimal depositAmount = new BigDecimal("1000000000000.00");

        assertThrows(InvalidAccountBalanceOperationException.class, () -> {
            bankAccountService.deposit(testBankAccount.getAccountNumber(), depositAmount);
        });
    }

    @Test
    void testWithdraw() throws BankAccountDoesNotExist, InterruptedException, InvalidAccountBalanceOperationException {
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
        testBankAccountOptional.get().setBalance(new BigDecimal("10.00"));
        when(bankAccountRepository.findByAccountNumber(testBankAccount.getAccountNumber()))
                .thenReturn(testBankAccountOptional);

        // ensure created and updated times can be different
        Thread.sleep(10);
        BigDecimal withdrawAmount = new BigDecimal("10.00");
        bankAccountService.withdraw(testBankAccount.getAccountNumber(), withdrawAmount);
        assertEquals(testBankAccountOptional.get().getBalance(), new BigDecimal("0.00"));
        assertNotEquals(testBankAccountOptional.get().getUpdated(), testBankAccountOptional.get().getCreated());
    }

    @Test
    void testWithdrawOverdrawn() {
        BankAccountService bankAccountService = new BankAccountServiceImpl(bankAccountRepository);
        when(bankAccountRepository.findByAccountNumber(testBankAccount.getAccountNumber()))
                .thenReturn(testBankAccountOptional);

        BigDecimal withdrawAmount = new BigDecimal("10.00");
        assertThrows(InvalidAccountBalanceOperationException.class, () -> {
            bankAccountService.withdraw(testBankAccount.getAccountNumber(), withdrawAmount);
        });
    }
}