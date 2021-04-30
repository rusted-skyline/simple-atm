package com.bank909.atm.repository;

import com.bank909.atm.entity.BankAccount;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

public class BankAccountRepositoryImpl implements BankAccountRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<BankAccount> findByAccountNumber(Long accountNumber) {
        Query query = entityManager.createNativeQuery("SELECT ba.* FROM bank_account as ba " +
                "WHERE ba.account_number = ? LIMIT 1", BankAccount.class);
        query.setParameter(1, accountNumber);

        return Optional.ofNullable((BankAccount)query.getSingleResult());
    }
}
