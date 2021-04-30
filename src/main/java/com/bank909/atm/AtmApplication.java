package com.bank909.atm;

import com.bank909.atm.service.AuthenticationService;
import com.bank909.atm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;

@SpringBootApplication
public class AtmApplication implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(AtmApplication.class);

    private static Session session;

    private static AuthenticationService authService;

    public AtmApplication(AuthenticationService authService) {
        this.authService = authService;
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

        for(;;) {
            if (session == null) {

                char[] customerId = console.readPassword("Enter your Customer ID: ");
                char[] pin = console.readPassword("Enter your PIN: ");

                if (authService.isAuthenticated(Long.valueOf(String.valueOf(customerId)), String.valueOf(pin))) {
                    log.info("AUTHED");
                    session = new Session();
                }
                continue;

            } else if (!session.isExpired()) {
                console.readLine("Choices: ");
            } else {
                console.printf("Session expired, please re-authenticate");
                session = null;
            }
        }
    }
}
