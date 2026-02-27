package carsharing.userinterface;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.repository.Repository;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

public class DesktopApp {
    private final Repository repository;
    private final JFrame frame;

    public DesktopApp(Repository repository) {
        this.repository = repository;
        this.frame = new JFrame("Car Sharing");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(640, 420);
        this.frame.setLocationRelativeTo(null);
    }

    public void run() {
        enableSystemLookAndFeel();
        frame.setVisible(true);
        showMainMenu();
        frame.dispose();
    }

    private void enableSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep default look and feel if system look and feel is unavailable.
        }
    }

    private void showMainMenu() {
        boolean running = true;
        while (running) {
            String[] options = {
                    "Log in as manager",
                    "Log in as customer",
                    "Create a customer",
                    "Exit"
            };
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Select an action",
                    "Car Sharing",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                showManagerMenu();
            } else if (choice == 1) {
                loginAsCustomer();
            } else if (choice == 2) {
                createCustomer();
            } else {
                running = false;
            }
        }
    }

    private void showManagerMenu() {
        boolean running = true;
        while (running) {
            String[] options = {
                    "Company list",
                    "Create a company",
                    "Back"
            };
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Manager menu",
                    "Manager",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                selectCompanyForManager();
            } else if (choice == 1) {
                createCompany();
            } else {
                running = false;
            }
        }
    }

    private void selectCompanyForManager() {
        List<Company> companies = repository.listCompanies();
        if (companies.isEmpty()) {
            showInfo("The company list is empty!");
            return;
        }

        SelectionItem<Company> selected = selectFromList(
                "Choose a company",
                "Companies",
                companies,
                Company::getName
        );
        if (selected != null) {
            showCompanyMenu(selected.value());
        }
    }

    private void createCompany() {
        String companyName = askForText("Enter the company name:");
        if (companyName == null) {
            return;
        }
        repository.addCompany(companyName);
        showInfo("The company was created!");
    }

    private void showCompanyMenu(Company company) {
        boolean running = true;
        while (running) {
            String[] options = {
                    "Car list",
                    "Create a car",
                    "Back"
            };
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "'" + company.getName() + "' company",
                    "Company",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                showCars(company);
            } else if (choice == 1) {
                createCar(company);
            } else {
                running = false;
            }
        }
    }

    private void showCars(Company company) {
        List<Car> cars = repository.listCarsByCompanyId(company.getId());
        if (cars.isEmpty()) {
            showInfo("The car list is empty!");
            return;
        }

        StringBuilder content = new StringBuilder("Car list:\n");
        for (int i = 0; i < cars.size(); i++) {
            content.append(i + 1)
                    .append(". ")
                    .append(cars.get(i).getName())
                    .append('\n');
        }
        showInfo(content.toString().trim());
    }

    private void createCar(Company company) {
        String carName = askForText("Enter the car name:");
        if (carName == null) {
            return;
        }
        repository.addCompanyCar(carName, company.getId());
        showInfo("The car was added!");
    }

    private void loginAsCustomer() {
        List<Customer> customers = repository.listCustomers();
        if (customers.isEmpty()) {
            showInfo("The customer list is empty!");
            return;
        }

        SelectionItem<Customer> selected = selectFromList(
                "Choose a customer",
                "Customers",
                customers,
                Customer::getName
        );
        if (selected != null) {
            showCustomerMenu(selected.value().getId());
        }
    }

    private void createCustomer() {
        String customerName = askForText("Enter the customer name:");
        if (customerName == null) {
            return;
        }
        repository.addCustomer(customerName);
        showInfo("The customer was added!");
    }

    private void showCustomerMenu(int customerId) {
        boolean running = true;
        while (running) {
            Customer customer = repository.findCustomerById(customerId);
            if (customer == null) {
                showError("Customer no longer exists.");
                return;
            }

            String[] options = {
                    "Rent a car",
                    "Return a rented car",
                    "My rented car",
                    "Back"
            };
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Customer: " + customer.getName(),
                    "Customer",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                rentCar(customer);
            } else if (choice == 1) {
                returnCar(customer);
            } else if (choice == 2) {
                showRentedCar(customer);
            } else {
                running = false;
            }
        }
    }

    private void rentCar(Customer customer) {
        if (customer.getRentedCarId() != null) {
            showInfo("You've already rented a car!");
            return;
        }

        List<Company> companies = repository.listCompanies();
        if (companies.isEmpty()) {
            showInfo("The company list is empty!");
            return;
        }

        SelectionItem<Company> selectedCompany = selectFromList(
                "Choose a company",
                "Companies",
                companies,
                Company::getName
        );
        if (selectedCompany == null) {
            return;
        }

        List<Car> availableCars = repository.listAvailableCarsByCompanyId(selectedCompany.value().getId());
        if (availableCars.isEmpty()) {
            showInfo("The car list is empty!");
            return;
        }

        SelectionItem<Car> selectedCar = selectFromList(
                "Choose a car",
                "Cars",
                availableCars,
                Car::getName
        );
        if (selectedCar == null) {
            return;
        }

        customer.setRentedCarId(selectedCar.value().getId());
        repository.updateCustomer(customer);
        showInfo("You rented '" + selectedCar.value().getName() + "'");
    }

    private void returnCar(Customer customer) {
        if (customer.getRentedCarId() == null) {
            showInfo("You didn't rent a car!");
            return;
        }
        customer.setRentedCarId(null);
        repository.updateCustomer(customer);
        showInfo("You've returned a rented car!");
    }

    private void showRentedCar(Customer customer) {
        if (customer.getRentedCarId() == null) {
            showInfo("You didn't rent a car!");
            return;
        }

        Car car = repository.findCarById(customer.getRentedCarId());
        if (car == null) {
            showError("Car no longer exists in database!");
            return;
        }
        Company company = repository.findCompanyById(car.getCompanyId());
        if (company == null) {
            showError("Company no longer exists in database!");
            return;
        }

        String message = "Your rented car:\n" + car.getName() + "\nCompany:\n" + company.getName();
        showInfo(message);
    }

    private String askForText(String prompt) {
        String value = JOptionPane.showInputDialog(frame, prompt);
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            showInfo("Input cannot be empty.");
            return null;
        }
        return trimmed;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Car Sharing", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Car Sharing", JOptionPane.ERROR_MESSAGE);
    }

    private <T> SelectionItem<T> selectFromList(
            String prompt,
            String title,
            List<T> values,
            LabelProvider<T> labelProvider
    ) {
        List<SelectionItem<T>> items = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            items.add(new SelectionItem<>(i + 1, labelProvider.label(values.get(i)), values.get(i)));
        }

        Object selected = JOptionPane.showInputDialog(
                frame,
                prompt,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                items.toArray(),
                items.get(0)
        );
        if (selected == null) {
            return null;
        }
        return (SelectionItem<T>) selected;
    }

    private interface LabelProvider<T> {
        String label(T value);
    }

    private record SelectionItem<T>(int index, String label, T value) {
        @Override
        public String toString() {
            return index + ". " + label;
        }
    }
}
