# Simple ATM

This application provides a simple console-based interface that models a very 
primitive ATM interface.

## Requirements

* Java 8
* Linux-based Console (tested with Bash on Mac OS X Big Sur)

## Build and Test

Compile the code:
```
./build.sh
```

Run the tests:
```
./test.sh
```

## Usage

Run the application:
```
./run.sh
```

### Test Credentials

The application is pre-built with test credentials

```
Account number: 1234567812345678
PIN: 1234
```

## Assumptions

1. Only one customer can login at a time
1. User and account creation out of scope

## Decisions and Tradeoffs

1. I chose Spring JPA because the data model required for this application is simple and JPA offers
   quick, testable data layer creation with the possibiliy for extension
   in the future if more complex or performancen drive data layer requirements
   were to arise.
1. I chose to use `SpringBootApplication` for the core application driver because
   it offers the dependency injection and auto-wiring capabilities of Spring
   with the ability to implement the `CommandLineRunner` interface.  This means
   less boilerplate while easily being able to wire up the data layer.
1. I chose to use `java.io.Console` because it provides a convenient way to get user input, particularly 
   the `readPassword()` method, which will suppress printing input characters 
   to the console for additional security.
1. I chose to make a custom `Session` object with a 5 minute timeout to emulate a standard
   security pattern of limited session time.  Upon expiration re-authentication is required.
1. I chose to refactor out the core console application logic into a separate,
   static class `AtmApplicationHelper` which helped simplify the main `AtmApplication`
   class and make the core application business logic easier to test.  However,
   this made `AtmApplication` harder to test because of the need to mock 
   static method calls, which cannot be done with Mockito.  It can be done with PowerMock but for the sake
   of this project I chose to cut that from scope.  This meant not having test coverage 
   for `AtmApplication`.
1. I chose the PBKDF2 algorithm for password hashing.  This uses 
   a salt to ensure hash uniqueness and configurable iteration to increase the
   work factor of the algorithm which helps mitigate brute forcing the hash. An alternative
   I considered is bcrypt which has smaller storage requirements.  See the "Next Steps" 
   section for further discussion of future improvements that could be made.
1. I chose to make the maximum allowable deposit, withdraw, or balance amount $9999999999.99, which
   is one penny shy of 1 trillion dollars.  This was an arbitrary decision made by me
   for this project, but something I would not pick without consulting the business in a business setting.

## Next Steps I Would Explore

1. In order to scale `BankAccount` transactions and ensure strong data consistency I would
   choose a relational database like MySQL that could handle high transaction
   isolation (e.g. `REPEATABLE READ`).  This would be important if this service were ported to
   a microservice and there were more than one service committing transactions.
1. I initially thought about creating a record of all transactions but cut that from the scope
   of this project when I realized it wouldn't be necessary.  This could easily be done
   in the scope of this architecture by defining a `BankTransaction` type and appending new timestamped records 
   on every withdraw or deposit transaction.
1. I was not able to find a suitable, pre-packaged OSS PBKDF2 SDK so I chose to use a 
   clean implementation of PBKDF2 that I found.  In a business setting
   I would prefer to use a battle-tested password hashing library as is preferred
   for any critical security task.  A different solution like bcrypt could
   require less storage and reduce cost.
1. If `BankAccount` creation were added as a requirement there would need to be a way to generate
   unique account numbers.  This is far more complex than the naive 16 digit number
   scheme I have used and involves use of and compliance with several standards (e.g. ISO/IEC 7812,
   ISO-3166-1, bank identification numbers (BIN), etc).
