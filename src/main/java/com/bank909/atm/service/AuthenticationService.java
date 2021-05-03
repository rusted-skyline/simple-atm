package com.bank909.atm.service;

public interface AuthenticationService {
    /**
     * Authenticates userId and pin against stored credentials.
     *
     * @param userId userId of an account owner
     * @param pin an account pin number
     * @return boolean whether user is authenticated
     */
    boolean isAuthenticated(Long userId, String pin);

}
