package com.bank909.atm.service;

public interface AuthenticationService {

    public boolean isAuthenticated(Long userId, String pin);

}
