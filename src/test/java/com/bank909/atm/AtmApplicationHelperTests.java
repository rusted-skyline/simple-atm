package com.bank909.atm;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.entity.User;
import com.bank909.atm.exception.AuthenticationException;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;
import com.bank909.atm.exception.InvalidInputException;
import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.service.BankAccountService;
import com.bank909.atm.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Console;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class AtmApplicationTests {

    private BankAccountService bankAccountService;
    private AuthenticationService authenticationService;
    private Console console;
    private String accountNumber;
    private String pin;
    private User testUser;
    private BankAccount testBankAccount;
    private Optional<BankAccount> testBankAccountOptional;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setup() {
        bankAccountService = mock(BankAccountService.class);
        authenticationService = mock(AuthenticationService.class);
        console = mock(Console.class);
        accountNumber = "1111222233334444";
        pin = "1111";

        OffsetDateTime created = OffsetDateTime.now();

        testUser = new User();
        testUser.setId(new Long(1));
        testUser.setCreated(created);
        testUser.setPin(pin);

        testBankAccount = new BankAccount();
        testBankAccount.setBalance(new BigDecimal(0));
        testBankAccount.setAccountNumber(new Long("1111222233334444"));
        testBankAccount.setAccountNumber(new Long(1));
        testBankAccount.setUser(testUser);
        testBankAccount.setCreated(created);
        testBankAccount.setCreated(created);
        testBankAccountOptional = Optional.of(testBankAccount);
    }

    @Test
    void testAuthenticate() throws AuthenticationException, InvalidInputException {
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(authenticationService.isAuthenticated(testUser.getId(), pin)).thenReturn(true);
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn(accountNumber);
        when(console.readPassword("Enter your PIN: ")).thenReturn(pin.toCharArray());

        Session session = AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        assertEquals(session.getAccountNumber(), Long.valueOf(accountNumber));
        assertEquals(session.isExpired(), false);
    }

    @Test
    void testAuthenticateInvalidAccountNumberNotANumber() {
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn("invalid");

        assertThrows(InvalidInputException.class, () -> {
            AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        });
    }

    @Test
    void testAuthenticateInvalidAccountNumberSize() {
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn("111111111111111");

        assertThrows(InvalidInputException.class, () -> {
            AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        });
    }

    @Test
    void testAuthenticateInvalidPinNotANumber() {
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn(accountNumber);
        when(console.readPassword("Enter your PIN: ")).thenReturn("invalid".toCharArray());

        assertThrows(InvalidInputException.class, () -> {
            AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        });
    }

    @Test
    void testAuthenticateInvalidPinSize() {
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn(accountNumber);
        when(console.readPassword("Enter your PIN: ")).thenReturn("123".toCharArray());

        assertThrows(InvalidInputException.class, () -> {
            AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        });
    }

    @Test
    void testAuthenticateAuthException() {
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(authenticationService.isAuthenticated(testUser.getId(), pin)).thenReturn(false);
        when(console.readLine("Enter your 16 digit account number: ")).thenReturn(accountNumber);
        when(console.readPassword("Enter your PIN: ")).thenReturn(pin.toCharArray());

        assertThrows(AuthenticationException.class, () -> {
            AtmApplicationHelper.authenticate(console, bankAccountService, authenticationService);
        });
    }

    @Test
    void testPerformTransactionGetBalance() throws InvalidInputException, BankAccountDoesNotExist, InsufficientBalanceException {
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(console.readLine(AtmApplicationHelper.CHOICE_PROMPT)).thenReturn(AtmApplicationHelper.CHOICE_ONE);

        AtmApplicationHelper.performTransaction(console,
                new Session(Long.valueOf(accountNumber)),
                bankAccountService);
        verify(bankAccountService).findByAccountNumber(Long.valueOf(accountNumber));
    }

    @Test
    void testPerformTransactionDeposit() throws InvalidInputException, BankAccountDoesNotExist, InsufficientBalanceException {
        String depositAmount = "1.00";
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(console.readLine(AtmApplicationHelper.CHOICE_PROMPT)).thenReturn(AtmApplicationHelper.CHOICE_TWO);
        when(console.readLine("How much would you like to deposit? (x.xx) \n> ")).thenReturn(depositAmount);

        AtmApplicationHelper.performTransaction(console,
                new Session(Long.valueOf(accountNumber)),
                bankAccountService);
        verify(bankAccountService).deposit(Long.valueOf(accountNumber), new BigDecimal(depositAmount));
        verify(bankAccountService).findByAccountNumber(Long.valueOf(accountNumber));
    }

    @Test
    void testPerformTransactionWithdraw() throws InvalidInputException, BankAccountDoesNotExist, InsufficientBalanceException {
        String withdrawAmount = "1.00";
        testBankAccountOptional.get().setBalance(new BigDecimal("10.00"));
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(console.readLine(AtmApplicationHelper.CHOICE_PROMPT)).thenReturn(AtmApplicationHelper.CHOICE_THREE);
        when(console.readLine("How much would you like to withdraw? (x.xx) \n> ")).thenReturn(withdrawAmount);

        AtmApplicationHelper.performTransaction(console,
                new Session(Long.valueOf(accountNumber)),
                bankAccountService);
        verify(bankAccountService).withdraw(Long.valueOf(accountNumber), new BigDecimal(withdrawAmount));
        verify(bankAccountService).findByAccountNumber(Long.valueOf(accountNumber));
    }

    @Test
    void testPerformTransactionExit() throws InvalidInputException, BankAccountDoesNotExist, InsufficientBalanceException {
        when(bankAccountService.findByAccountNumber(Long.valueOf(accountNumber))).thenReturn(testBankAccountOptional);
        when(console.readLine(AtmApplicationHelper.CHOICE_PROMPT)).thenReturn(AtmApplicationHelper.CHOICE_THREE);

        AtmApplicationHelper.performTransaction(console,
                new Session(Long.valueOf(accountNumber)),
                bankAccountService);

        verify(console.printf("Thanks for choosing " + AtmApplicationHelper.BANK_NAME + " and have a great day!\n"));
    }

    @Test
    void testPerformTransactionInvalidChoice() {
        when(console.readLine(AtmApplicationHelper.CHOICE_PROMPT)).thenReturn("99");

        assertThrows(InvalidInputException.class, () -> {
            AtmApplicationHelper.performTransaction(console,
                    new Session(Long.valueOf(accountNumber)),
                    bankAccountService);
        });
    }

}