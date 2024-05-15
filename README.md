# Car Sharing - Command-Line interface with JDBC Connection and H2 Database

## About
Car-sharing is becoming a more and more popular green alternative to owning a car. Let's create a program that manages a car-sharing service allowing companies to rent out their cars and find customers.

Here's the link to the project: https://hyperskill.org/projects/140

Check out my profile: https://hyperskill.org/profile/500961738

All documentation retrieved from https://hyperskill.org/projects/140, provided by JetBrains Academy.

## Documentation
The program allows to log in as a manger or as a customer (or create new customers).

A manager can list or create new companies, and also list or create new cars for a selected company.

If the log in as a customer option was chosen, the program will print the list of existing customers and prompt the user to choose one.

A customer can either rent a car from the list of *available* cars from given companies, return a rented car or show a previously rented car (if any).


## Usage
After running the program, it should create the database file in the root folder.
The database file name is obtained from the command-line arguments:
```text
-databaseFileName <filename>
```
If -databaseFileName argument is not given, then the database file name can be "anything".

## Main menu
```text
1. Log in as a manager
2. Log in as a customer
3. Create a customer
0. Exit
```

###  Manager menu
```text
1. Company list
2. Create a company
0. Back
```

### Customer menu
```text
Choose a customer:
1. First customer
2. Second customer
0. Back
```

### Rent a car menu
```text
1. Rent a car
2. Return a rented car
3. My rented car
0. Back
```
