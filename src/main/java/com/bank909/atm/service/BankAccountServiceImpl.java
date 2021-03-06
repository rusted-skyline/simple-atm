package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InvalidAccountBalanceOperationException;
import com.bank909.atm.repository.BankAccountRepository;
import com.bank909.atm.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(Long accountNumber) {
        Optional<BankAccount> bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
        return bankAccount;
    }

    @Override
    @Transactional
    public void deposit(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist, InvalidAccountBalanceOperationException {
        Optional<BankAccount> bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
        if (bankAccount.isPresent()) {
            BankAccount account = bankAccount.get();

            // ensure deposit would not be greater maximum allowed account balance
            if ((account.getBalance().add(amount)).compareTo(Constants.MAX_AMOUNT) <= 0) {
                account.setBalance(account.getBalance().add(amount));
                account.setUpdated(OffsetDateTime.now());
                bankAccountRepository.save(account);
            } else {
                throw new InvalidAccountBalanceOperationException("Deposit exceeds maximum balance.");
            }
        } else {
            throw new BankAccountDoesNotExist("Account does not exist.");
        }
    }

    @Override
    @Transactional
    public void withdraw(Long accountNumber, BigDecimal amount) throws BankAccountDoesNotExist, InvalidAccountBalanceOperationException {
        Optional<BankAccount> bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
        if (bankAccount.isPresent()) {
            BankAccount account = bankAccount.get();

            // ensure withdraw would not be more than current account balance
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InvalidAccountBalanceOperationException("Insuffient balance in account.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            account.setUpdated(OffsetDateTime.now());
            bankAccountRepository.save(account);
        } else {
            throw new BankAccountDoesNotExist("Account does not exist.");
        }
    }
}
