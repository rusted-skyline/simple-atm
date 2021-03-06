package com.bank909.atm;

import com.bank909.atm.exception.*;
import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.service.BankAccountService;
import com.bank909.atm.session.Session;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;

/**
 * A simple, console-based interface that models a very primitive ATM interface.
 * Main runtime entry point into the app.
 */
@SpringBootApplication
public class AtmApplication implements CommandLineRunner {

    private static Session session;
    private static AuthenticationService authService;
    private static BankAccountService bankAccountService;

    // prefix for printing errors to the console
    private final String ERROR_PREFIX = "Error: ";

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
        console.printf("#  Welcome to " + AtmApplicationHelper.BANK_NAME + "!  #\n");
        console.printf("##########################\n");
        for(;;) {
            if (session == null) {
                try {
                    session = AtmApplicationHelper.authenticate(console, bankAccountService, authService);
                    console.printf("Successfully logged in!\n");
                    continue;
                } catch (InvalidInputException e) {
                    console.printf(ERROR_PREFIX + e.getMessage() + "\n");
                    continue;
                } catch (AuthenticationException e) {
                    console.printf(ERROR_PREFIX + e.getMessage() + " Please try again.\n");
                    continue;
                }

            } else if (!session.isExpired()) {
                try {
                    AtmApplicationHelper.performTransaction(console, session, bankAccountService);
                } catch (InvalidInputException e) {
                    console.printf(ERROR_PREFIX + e.getMessage() + "\n");
                    continue;
                } catch (BankAccountDoesNotExist e) {
                    console.printf(ERROR_PREFIX + "Could not locate account.\n");
                    continue;
                } catch (InvalidAccountBalanceOperationException e) {
                    console.printf(ERROR_PREFIX + "Invalid deposit or withdraw operation.");
                    continue;
                }

            } else {
                console.printf(ERROR_PREFIX + "Session expired, please re-authenticate.\n");
                session = null;
            }
        }
    }



}
