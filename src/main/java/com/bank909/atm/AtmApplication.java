package com.bank909.atm;

import com.bank909.atm.entity.BankAccount;
import com.bank909.atm.exception.BankAccountDoesNotExist;
import com.bank909.atm.exception.InsufficientBalanceException;
import com.bank909.atm.repository.BankAccountRepository;
import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.service.BankAccountService;
import com.bank909.atm.session.Session;
import com.bank909.atm.util.CurrencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.Console;
import java.math.BigDecimal;
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
        Long customerId = null;
        String pin = null;

        if (console == null) {
            System.out.println("No console available.  Please run this application from a real terminal");
            return;
        }

        console.printf("###################################\n");
        console.printf("#  Welcome to " + BANK_NAME + "!  #\n");
        console.printf("###################################\n");
        for(;;) {
            if (session == null) {
                customerId = Long.valueOf(String.valueOf(console.readPassword("Enter your Customer ID: ")));
                pin = String.valueOf(console.readPassword("Enter your PIN: "));

                //TODO: Validate inputs

                if (authService.isAuthenticated(Long.valueOf(String.valueOf(customerId)), String.valueOf(pin))) {
                    session = new Session();
                    console.printf("Successfully logged in!\n");
                } else {
                    console.printf("Incorrect Customer ID or PIN.  Please try again.\n");
                }
                continue;

            } else if (!session.isExpired()) {
                String choices = "\nPlease enter a choice:\n" +
                        "  [" + CHOICE_ONE + "] View Balance\n" +
                        "  [" + CHOICE_TWO + "] Make Deposit\n" +
                        "  [" + CHOICE_THREE + "] Make Withdraw\n" +
                        "  [" + CHOICE_FOUR + "] Logout\n" +
                        "> ";
                String choice = console.readLine(choices);
                Optional<BankAccount> account = bankAccountService.findById(Long.valueOf(customerId));

                switch(choice) {
                    case CHOICE_ONE:
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_TWO:
                        String depositAmount = console.readLine("How much would you like to deposit? (x.xx) \n> ");

                        bankAccountService.deposit(customerId, new BigDecimal(depositAmount));
                        account = bankAccountService.findById(Long.valueOf(customerId));
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_THREE:
                        String withdrawAmount = console.readLine("How much would you like to withdraw? (x.xx) \n> ");
                        try {
                            bankAccountService.withdraw(customerId, new BigDecimal(withdrawAmount));
                        } catch (InsufficientBalanceException e) {
                            console.printf("You do not have sufficient funds to withdraw that amount.\n");
                        }

                        account = bankAccountService.findById(Long.valueOf(customerId));
                        printBalance(console, account.get().getBalance());
                        break;
                    case CHOICE_FOUR:
                        console.printf("Thanks for choosing Bank 909 and have a great day!\n");
                        System.exit(0);
                }
            } else {
                console.printf("Session expired, please re-authenticate.\n");
                session = null;
                customerId = null;
                pin = null;
            }
        }
    }

    private void printBalance(Console console, BigDecimal balance) {
        console.printf("Your account balance is: " + CurrencyUtil.formatUSDCurrencyString(balance) + "\n");
    }
}
