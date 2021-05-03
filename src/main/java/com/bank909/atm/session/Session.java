package com.bank909.atm.session;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * In-memory session with hard-coded 5 minute timeout.
 * Holds an account number of current logged in user.
 */
public class Session {
    private LocalDateTime created;
    private Long accountNumber;
    private int MAX_TIME = 60*5; // seconds

    public Session(Long accountNumber) {
        this.accountNumber = accountNumber;
        this.created = LocalDateTime.now();
    }

    public boolean isExpired() {
        if (Duration.between(this.created, LocalDateTime.now()).getSeconds() > MAX_TIME) {
            return true;
        }
        return false;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }
}
