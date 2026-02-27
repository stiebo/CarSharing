package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.repository.Repository;

import java.util.List;

public class CustomerService {
    private final Repository repository;

    public CustomerService(Repository repository) {
        this.repository = repository;
    }

    public List<Customer> getCustomers() {
        return repository.listCustomers();
    }

    public void addCustomer(String name) {
        repository.addCustomer(name);
    }

    public Customer refreshCustomer(Customer customer) {
        return repository.findCustomerById(customer.getId());
    }

    public List<Company> getCompanies() {
        return repository.listCompanies();
    }

    public List<Car> getAvailableCarsForCompany(Company company) {
        return repository.listAvailableCarsByCompanyId(company.getId());
    }

    public Car getRentedCar(Customer customer) {
        if (customer.getRentedCarId() == null) return null;
        return repository.findCarById(customer.getRentedCarId());
    }

    public Company getCompanyForCar(Car car) {
        return repository.findCompanyById(car.getCompanyId());
    }

    public void rentCarForCustomer(Customer customer, Car car) {
        customer.setRentedCarId(car.getId());
        repository.updateCustomer(customer);
    }

    public void returnCarForCustomer(Customer customer) {
        customer.setRentedCarId(null);
        repository.updateCustomer(customer);
    }
}

