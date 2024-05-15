package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.userinterface.CustomerMenu;
import carsharing.userinterface.Menu;
import carsharing.userinterface.MenuEntry;
import carsharing.userinterface.UserInput;
import carsharing.repository.Repository;

import java.util.List;

public class CustomerService {
    private final Repository repository;

    public CustomerService(Repository repository) {
        this.repository = repository;
    }

    public void customerLogin() {
        List<Customer> customers = repository.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            return;
        }
        Menu chooseCustomerMenu = new Menu("Customer list:");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            chooseCustomerMenu.addMenuEntry(new MenuEntry(i + 1, customer.getName(), () -> {
                this.showCustomer(customer);
                chooseCustomerMenu.exit();
            }));
        }
        chooseCustomerMenu.addMenuBack();
        chooseCustomerMenu.run();
    }

    public void createCustomer() {
        String customerName = UserInput.getInput("Enter the customer name:" + System.lineSeparator());
        repository.addCustomer(customerName);
        System.out.println("The customer was added!");
    }

    public void showCustomer(Customer customer) {
        new CustomerMenu(this, customer).run();
    }

    public void rentCar (Customer customer) {
        List<Company> companies = repository.listCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return;
        }
        if (customer.getRented_car_id() != null) {
            System.out.println("You've already rented a car!");
            return;
        }
        Menu chooseCompanyMenu = new Menu("Choose a company:");
        for (int i = 0; i < companies.size(); i++) {
            Company company = companies.get(i);
            chooseCompanyMenu.addMenuEntry(new MenuEntry(i + 1, company.getName(), () -> {
                this.chooseCar(customer, company);
                chooseCompanyMenu.exit();
            }));
        }
        chooseCompanyMenu.addMenuBack();
        chooseCompanyMenu.run();

    }

    public void chooseCar(Customer customer, Company company) {
        List<Car> availableCars = repository.listAvailableCarsByCompanyId(company.getId());
        if (availableCars.isEmpty()) {
            System.out.println("The car list is empty!");
            return;
        }
        Menu chooseCarMenu = new Menu("Choose a car:");
        for (int i = 0; i < availableCars.size(); i++) {
            Car car = availableCars.get(i);
            chooseCarMenu.addMenuEntry(new MenuEntry(i+1, car.getName(), () -> {
                this.rentThisCar(customer, car);
                chooseCarMenu.exit();
            }));
        }
        chooseCarMenu.addMenuBack();
        chooseCarMenu.run();
    }

    public void rentThisCar (Customer customer, Car car) {
        customer.setRented_car_id(car.getId());
        repository.updateCustomer(customer);
        System.out.printf("You rented '%s'%n", car.getName());
    }

    public void returnCar (Customer customer) {
        if (customer.getRented_car_id() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }
        customer.setRented_car_id(null);
        repository.updateCustomer(customer);
        System.out.println("You've returned a rented car!");
    }

    public void showRentedCar (Customer customer) {
        if (customer.getRented_car_id() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }
        Car car = repository.findCarById(customer.getRented_car_id());
        if (car == null) {
            throw new RuntimeException("Car no longer exists in database!");
        }
        Company company = repository.findCompanyById(car.getCompanyId());
        System.out.println("Your rented car:");
        System.out.println(car.getName());
        System.out.println("Company:");
        System.out.println(company.getName());
    }
}
