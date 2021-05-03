package com.bank909.atm.service;

import com.bank909.atm.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static com.bank909.atm.service.PasswordValidator.validatePassword;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final UserService userService;

    public AuthenticationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    public boolean isAuthenticated(Long userId, String pin) {
        Optional<User> user = this.userService.findById(userId);

        if (!user.isPresent()) {
            return false;
        }
        try {
            if (validatePassword(pin, user.get().getPin())) {
                return true;
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        return false;
    }


}
