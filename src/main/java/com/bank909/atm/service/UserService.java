package com.bank909.atm.service;

import com.bank909.atm.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

}
