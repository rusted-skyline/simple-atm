package com.bank909.atm.repository;

import com.bank909.atm.entity.BankAccount;

import java.util.Optional;

public interface BankAccountRepositoryCustom {

    Optional<BankAccount> findByAccountNumber(Long accountNumber);

}
