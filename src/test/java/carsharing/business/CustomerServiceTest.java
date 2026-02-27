package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.repository.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private Repository repository;

    private CustomerService customerService;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(repository);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void rentCar_alreadyRenting_printsGuardMessage() {
        Customer customer = new Customer(1, "Alice", 42);
        customerService.rentCar(customer);
        assertTrue(outContent.toString().contains("You've already rented a car!"));
        verify(repository, never()).listCompanies();
    }

    @Test
    void returnCar_notRenting_printsGuardMessage() {
        Customer customer = new Customer(1, "Bob", null);
        customerService.returnCar(customer);
        assertTrue(outContent.toString().contains("You didn't rent a car!"));
        verify(repository, never()).updateCustomer(any());
    }

    @Test
    void returnCar_withRental_clearsRentedCarId() {
        Customer customer = new Customer(1, "Carol", 5);
        customerService.returnCar(customer);
        assertNull(customer.getRentedCarId());
        verify(repository).updateCustomer(customer);
        assertTrue(outContent.toString().contains("You've returned a rented car!"));
    }

    @Test
    void showRentedCar_notRenting_printsGuardMessage() {
        Customer customer = new Customer(1, "Dave", null);
        customerService.showRentedCar(customer);
        assertTrue(outContent.toString().contains("You didn't rent a car!"));
        verify(repository, never()).findCarById(anyInt());
    }

    @Test
    void showRentedCar_withRental_printsCarAndCompany() {
        Customer customer = new Customer(1, "Eve", 7);
        Car car = new Car(7, "Volvo", 3);
        Company company = new Company(3, "BigRent");
        when(repository.findCarById(7)).thenReturn(car);
        when(repository.findCompanyById(3)).thenReturn(company);

        customerService.showRentedCar(customer);

        String output = outContent.toString();
        assertTrue(output.contains("Volvo"));
        assertTrue(output.contains("BigRent"));
    }

    @Test
    void customerLogin_emptyCustomerList_printsEmptyMessage() {
        when(repository.listCustomers()).thenReturn(List.of());
        customerService.customerLogin();
        assertTrue(outContent.toString().contains("The customer list is empty!"));
    }
}
