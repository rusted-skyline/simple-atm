package com.bank909.atm.service;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;
import com.bank909.atm.repository.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final static Logger log = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
        return bankAccount;
    }

    @Override
    public void deposit(Long id, BigDecimal amount) throws BankAccountDoesNotExist {
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            BankAccount account = bankAccount.get();
            account.setBalance(account.getBalance().add(amount));
            log.info(String.valueOf(account.getBalance()));
            bankAccountRepository.save(account);
        } else {
            throw new BankAccountDoesNotExist();
        }
    }

    @Override
    public void withdraw(Long id, BigDecimal amount) throws BankAccountDoesNotExist, InsufficientBalanceException {
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isPresent()) {
            BankAccount account = bankAccount.get();
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException();
            }
            account.setBalance(account.getBalance().subtract(amount));
            bankAccountRepository.save(account);
        } else {
            throw new BankAccountDoesNotExist();
        }
    }
}
