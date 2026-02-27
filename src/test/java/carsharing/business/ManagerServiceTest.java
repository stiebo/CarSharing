package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
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
class ManagerServiceTest {

    @Mock
    private Repository repository;

    private ManagerService managerService;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        managerService = new ManagerService(repository);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void listCompany_emptyList_printsEmptyMessage() {
        when(repository.listCompanies()).thenReturn(List.of());
        managerService.listCompany();
        assertTrue(outContent.toString().contains("The company list is empty!"));
    }

    @Test
    void showCompanyCars_emptyCars_printsEmptyMessage() {
        Company company = new Company(1, "Hertz");
        when(repository.listCarsByCompanyId(1)).thenReturn(List.of());
        managerService.showCompanyCars(company);
        assertTrue(outContent.toString().contains("The car list is empty!"));
    }

    @Test
    void showCompanyCars_withCars_printsCarList() {
        Company company = new Company(1, "Hertz");
        List<Car> cars = List.of(new Car(1, "Tesla", 1), new Car(2, "BMW", 1));
        when(repository.listCarsByCompanyId(1)).thenReturn(cars);
        managerService.showCompanyCars(company);
        String output = outContent.toString();
        assertTrue(output.contains("Tesla"));
        assertTrue(output.contains("BMW"));
    }
}
