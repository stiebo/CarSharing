# Car Sharing - Desktop application with JDBC and H2

## About
Car-sharing is becoming a more and more popular green alternative to owning a car. Let's create a program that manages a car-sharing service allowing companies to rent out their cars and find customers.

Here's the link to the project: https://hyperskill.org/projects/140

Check out my profile: https://hyperskill.org/profile/500961738

All documentation retrieved from https://hyperskill.org/projects/140, provided by JetBrains Academy.

## Documentation
The application starts as a desktop window (Swing with native system look-and-feel on Windows).

From the main screen you can log in as manager, log in as customer, or create a new customer.

A manager can list companies, create a company, open a company, view its cars, and add a new car.

A customer can rent an available car, return a rented car, and view details about the currently rented car.


## Usage
After running the program, it should create the database file in the root folder.
The database file name is obtained from the command-line arguments:
```text
-databaseFileName <filename>
```
If -databaseFileName argument is not given, then the database file name can be "anything".

## Interface
The app uses window screens and dialog boxes instead of terminal menus.
