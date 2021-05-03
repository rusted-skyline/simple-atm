package com.bank909.atm.service;

import com.bank909.atm.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * Find a user by id
     *
     * @param id of user
     * @return a user
     */
    Optional<User> findById(Long id);

}
