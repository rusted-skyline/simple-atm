package com.bank909.atm.service;

import com.bank909.atm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceImplTests {

    private UserService userService;
    private PasswordValidator passwordValidator;

    private User testUser;
    private Optional<User> testUserOptional;

    private Long testUserId = new Long(1);
    private String testPin = "testpin";

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        passwordValidator = mock(PasswordValidator.class);

        testUser = new User();
        testUser.setPin(testPin);
        testUser.setId(testUserId);
        testUserOptional = Optional.of(testUser);
    }

    @Test
    void testIsAuthenticatedValidPassword() throws InvalidKeySpecException, NoSuchAlgorithmException {
        AuthenticationService authService = new AuthenticationServiceImpl(userService);

        when(userService.findById(testUserId)).thenReturn(testUserOptional);
        try (MockedStatic<PasswordValidator> validator = Mockito.mockStatic(PasswordValidator.class)) {
            validator.when(() -> PasswordValidator.validatePassword(testPin, testUserOptional.get().getPin()))
                    .thenReturn(true);

            assertEquals(authService.isAuthenticated(testUserId, testPin), true);
        }
    }

    @Test
    void isAuthenticatedInvalidPassword() throws InvalidKeySpecException, NoSuchAlgorithmException {
        AuthenticationService authService = new AuthenticationServiceImpl(userService);

        when(userService.findById(testUserId)).thenReturn(testUserOptional);
        try (MockedStatic<PasswordValidator> validator = Mockito.mockStatic(PasswordValidator.class)) {
            validator.when(() -> PasswordValidator.validatePassword(testPin, testUserOptional.get().getPin()))
                    .thenReturn(false);

            assertEquals(authService.isAuthenticated(testUserId, testPin), false);
        }
    }

    @Test
    void isAuthenticatedUserNotFound() throws InvalidKeySpecException, NoSuchAlgorithmException {
        AuthenticationService authService = new AuthenticationServiceImpl(userService);

        Long testUserId = new Long(1);
        String testPin = "testpin";

        Optional<User> testUserOptional = Optional.ofNullable(null);

        when(userService.findById(testUserId)).thenReturn(testUserOptional);
        assertEquals(authService.isAuthenticated(testUserId, testPin), false);
    }
}
