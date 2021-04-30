package com.bank909.atm;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;
import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.service.BankAccountService;
import com.bank909.atm.session.Session;
import com.bank909.atm.util.CurrencyUtil;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

@SpringBootApplication
public class AtmApplication implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(AtmApplication.class);

    private static Session session;
    private static AuthenticationService authService;
    private static BankAccountService bankAccountService;

    private static String BANK_NAME = "Bank 909";

    private final String CHOICE_ONE = "1";
    private final String CHOICE_TWO = "2";
    private final String CHOICE_THREE = "3";
    private final String CHOICE_FOUR = "4";

    private final int VALID_ACCOUNT_NUMBER_LENGTH = 16;
    private final int VALID_PIN_LENGTH = 4;

    public AtmApplication(AuthenticationService authService, BankAccountService bankAccountService) {
        this.authService = authService;
        this.bankAccountService = bankAccountService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AtmApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Console console = System.console();

        if (console == null) {
            System.out.println("No console available.  Please run this application from a real terminal");
            return;
        }

        console.printf("##########################\n");
        console.printf("#  Welcome to " + BANK_NAME + "!  #\n");
        console.printf("##########################\n");
        for(;;) {
            if (session == null) {
                String accountNumberStr = console.readLine("Enter your 16 digit account number: ");
                if (!accountNumberIsValid(accountNumberStr)) {
                    console.printf("Error: Invalid account number.\n");
                    continue;
                }
                Long accountNumber = Long.valueOf(accountNumberStr);

                String pin = String.valueOf(console.readPassword("Enter your PIN: "));
                if (!pinIsValid(pin)) {
                    console.printf("Error: Invalid pin.\n");
                    continue;
                }

                Optional<BankAccount> account = bankAccountService.findByAccountNumber(accountNumber);

                if (account.isPresent()) {
                    String userId = String.valueOf(account.get().getUser().getId());
                    if (authService.isAuthenticated(Long.valueOf(String.valueOf(userId)), pin)) {
                        session = new Session(accountNumber);
                        console.printf("Successfully logged in!\n");
                        continue;
                    }
                }
                console.printf("Error: Incorrect Customer ID or PIN.  Please try again.\n");
                continue;

            } else if (!session.isExpired()) {
                String choices = "\nPlease enter a choice:\n" +
                        "  [" + CHOICE_ONE + "] View Balance\n" +
                        "  [" + CHOICE_TWO + "] Make Deposit\n" +
                        "  [" + CHOICE_THREE + "] Make Withdraw\n" +
                        "  [" + CHOICE_FOUR + "] Logout\n" +
                        "> ";
                String choice = console.readLine(choices);

                switch(choice) {
                    case CHOICE_ONE:
                        Optional<BankAccount> account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_TWO:
                        String depositAmount = console.readLine("How much would you like to deposit? (x.xx) \n> ");
                        if (!amountIsValid(depositAmount)) {
                            console.printf("Error: Invalid amount.\n");
                            continue;
                        }

                        try {
                            bankAccountService.deposit(session.getAccountNumber(), new BigDecimal(depositAmount));
                        } catch (BankAccountDoesNotExist e) {
                            console.printf("Error: Could not locate account.\n");
                            continue;
                        }

                        account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_THREE:
                        String withdrawAmount = console.readLine("How much would you like to withdraw? (x.xx) \n> ");
                        if (!amountIsValid(withdrawAmount)) {
                            console.printf("Error: Invalid amount.\n");
                            continue;
                        }
                        try {
                            bankAccountService.withdraw(session.getAccountNumber(), new BigDecimal(withdrawAmount));
                        } catch (InsufficientBalanceException e) {
                            console.printf("Error: You do not have sufficient funds to withdraw that amount.\n");
                        } catch (BankAccountDoesNotExist e) {
                            console.printf("Error: Could not locate account.\n");
                            continue;
                        }

                        account = bankAccountService.findByAccountNumber(session.getAccountNumber());
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_FOUR:
                        console.printf("Thanks for choosing " + BANK_NAME + " and have a great day!\n");
                        System.exit(0);
                }
            } else {
                console.printf("Session expired, please re-authenticate.\n");
                session = null;
            }
        }
    }

    private void printBalance(Console console, BigDecimal balance) {
        console.printf("Your account balance is: " + CurrencyUtil.formatUSDCurrencyString(balance) + "\n");
    }

    private boolean accountNumberIsValid(String accountNumber) {
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

    private boolean pinIsValid(String pin) {
        if (pin.length() != VALID_PIN_LENGTH) {
            return false;
        }
        try {
            Long.valueOf(pin);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean amountIsValid(String amount) {
        BigDecimalValidator validator = CurrencyValidator.getInstance();
        BigDecimal validatedAmt = validator.validate(amount, Locale.US);
        if (validatedAmt == null) {
            return false;
        }
        return true;
    }
}
