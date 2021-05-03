package com.bank909.atm;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.AuthenticationException;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InvalidAccountBalanceOperationException;
import com.bank909.atm.exception.InvalidInputException;
import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.service.BankAccountService;
import com.bank909.atm.session.Session;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;

import java.io.Console;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.bank909.atm.util.Constants.MAX_AMOUNT;

/**
 * Helper methods that support AtmApplication.
 */
public class AtmApplicationHelper {

    public static final String BANK_NAME = "Bank 909";

    public static final String CHOICE_ONE = "1";
    public static final String CHOICE_TWO = "2";
    public static final String CHOICE_THREE = "3";
    public static final String CHOICE_FOUR = "4";
    public static final String CHOICE_PROMPT =
            "\nPlease enter a choice:\n" +
            "  [" + CHOICE_ONE + "] View Balance\n" +
            "  [" + CHOICE_TWO + "] Make Deposit\n" +
            "  [" + CHOICE_THREE + "] Make Withdraw\n" +
            "  [" + CHOICE_FOUR + "] Logout\n" +
            "> ";

    public static final int VALID_ACCOUNT_NUMBER_LENGTH = 16;
    public static final int VALID_PIN_LENGTH = 4;

    public static Session authenticate(Console console,
                                       BankAccountService bankAccountService,
                                       AuthenticationService authenticationService)
            throws AuthenticationException, InvalidInputException {

        String accountNumberStr = console.readLine("Enter your 16 digit account number: ");

        if (!accountNumberIsValid(accountNumberStr)) {
            throw new InvalidInputException("Invalid account number.");
        }
        Long accountNumber = Long.valueOf(accountNumberStr);

        String pin = String.valueOf(console.readPassword("Enter your PIN: "));
        if (!pinIsValid(pin)) {
            throw new InvalidInputException("Invalid pin.");
        }

        Optional<BankAccount> account = bankAccountService.findByAccountNumber(accountNumber);

        if (account.isPresent()) {
            String userId = String.valueOf(account.get().getUser().getId());
            if (authenticationService.isAuthenticated(Long.valueOf(String.valueOf(userId)), pin)) {
                return new Session(accountNumber);
            }
        }

        throw new AuthenticationException("Incorrect Customer ID or PIN.");
    }

    public static void performTransaction(Console console,
                                          Session session,
                                          BankAccountService bankAccountService)
            throws InvalidInputException, BankAccountDoesNotExist, InvalidAccountBalanceOperationException {
        String choice = console.readLine(CHOICE_PROMPT);

        switch (choice) {
            case CHOICE_ONE:
                Optional<BankAccount> account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                printBalance(console, account.get().getBalance());
                break;
            case CHOICE_TWO:
                String depositAmount = console.readLine("How much would you like to deposit? (x.xx) \n> ");
                if (!amountIsValid(depositAmount)) {
                    throw new InvalidInputException("Invalid amount.");
                }

                bankAccountService.deposit(session.getAccountNumber(), new BigDecimal(depositAmount));

                account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                printBalance(console, account.get().getBalance());
                break;
            case CHOICE_THREE:
                String withdrawAmount = console.readLine("How much would you like to withdraw? (x.xx) \n> ");
                if (!amountIsValid(withdrawAmount)) {
                    throw new InvalidInputException("Invalid amount.");
                }

                bankAccountService.withdraw(session.getAccountNumber(), new BigDecimal(withdrawAmount));

                account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                printBalance(console, account.get().getBalance());
                break;
            case CHOICE_FOUR:
                console.printf("Thanks for choosing " + BANK_NAME + " and have a great day!\n");
                System.exit(0);
            default:
                throw new InvalidInputException("Invalid choice.");
        }

    }

    public static void printBalance(Console console, BigDecimal balance) {
        console.printf("Your account balance is: " + formatUSDCurrencyString(balance) + "\n");
    }

    public static String formatUSDCurrencyString(BigDecimal amount) {
        Locale us = new Locale("en", "US");
        Currency dollars = Currency.getInstance(us);
        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(us);
        return usdFormat.format(amount);
    }

    public static boolean amountIsValid(String amount) {
        BigDecimalValidator validator = CurrencyValidator.getInstance();
        BigDecimal validatedAmt = validator.validate(amount, Locale.US);
        if (validatedAmt == null) {
            return false;
        }
        if (validatedAmt.compareTo(MAX_AMOUNT) > 0) {
            return false;
        }
        if (validatedAmt.compareTo(new BigDecimal(0)) < 0) {
            return false;
        }
        return true;
    }

    public static boolean accountNumberIsValid(String accountNumber) {
        if (accountNumber.length() != VALID_ACCOUNT_NUMBER_LENGTH) {
            return false;
        }
        try {
            Long.valueOf(accountNumber);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean pinIsValid(String pin) {
        if (pin.length() != VALID_PIN_LENGTH) {
            return false;
        }
        try {
            Long.valueOf(pin);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

