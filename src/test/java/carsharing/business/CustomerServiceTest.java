package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private Repository repository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(repository);
    }

    @Test
    void getCustomers_returnsCustomerList() {
        List<Customer> customers = List.of(new Customer(1, "Alice", null), new Customer(2, "Bob", 3));
        when(repository.listCustomers()).thenReturn(customers);
        assertEquals(2, customerService.getCustomers().size());
    }

    @Test
    void addCustomer_delegatesToRepository() {
        customerService.addCustomer("Alice");
        verify(repository).addCustomer("Alice");
    }

    @Test
    void getRentedCar_notRenting_returnsNull() {
        Customer customer = new Customer(1, "Bob", null);
        assertNull(customerService.getRentedCar(customer));
        verify(repository, never()).findCarById(anyInt());
    }

    @Test
    void getRentedCar_withRental_returnsCarFromRepository() {
        Customer customer = new Customer(1, "Alice", 7);
        Car car = new Car(7, "Volvo", 3);
        when(repository.findCarById(7)).thenReturn(car);
        assertEquals(car, customerService.getRentedCar(customer));
    }

    @Test
    void rentCarForCustomer_setsRentedCarIdAndUpdates() {
        Customer customer = new Customer(1, "Carol", null);
        Car car = new Car(5, "BMW", 2);
        customerService.rentCarForCustomer(customer, car);
        assertEquals(5, customer.getRentedCarId());
        verify(repository).updateCustomer(customer);
    }

    @Test
    void returnCarForCustomer_clearsRentedCarIdAndUpdates() {
        Customer customer = new Customer(1, "Carol", 5);
        customerService.returnCarForCustomer(customer);
        assertNull(customer.getRentedCarId());
        verify(repository).updateCustomer(customer);
    }

    @Test
    void getCompanyForCar_delegatesToRepository() {
        Car car = new Car(7, "Volvo", 3);
        Company company = new Company(3, "BigRent");
        when(repository.findCompanyById(3)).thenReturn(company);
        assertEquals(company, customerService.getCompanyForCar(car));
    }

    @Test
    void refreshCustomer_returnsUpdatedCustomerFromRepository() {
        Customer stale = new Customer(1, "Dave", null);
        Customer fresh = new Customer(1, "Dave", 9);
        when(repository.findCustomerById(1)).thenReturn(fresh);
        assertEquals(fresh, customerService.refreshCustomer(stale));
    }
}
