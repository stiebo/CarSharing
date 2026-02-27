package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
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
class ManagerServiceTest {

    @Mock
    private Repository repository;

    private ManagerService managerService;

    @BeforeEach
    void setUp() {
        managerService = new ManagerService(repository);
    }

    @Test
    void getCompanies_emptyList_returnsEmptyList() {
        when(repository.listCompanies()).thenReturn(List.of());
        assertTrue(managerService.getCompanies().isEmpty());
    }

    @Test
    void getCompanies_withCompanies_returnsList() {
        List<Company> companies = List.of(new Company(1, "Hertz"), new Company(2, "Avis"));
        when(repository.listCompanies()).thenReturn(companies);
        assertEquals(2, managerService.getCompanies().size());
    }

    @Test
    void addCompany_delegatesToRepository() {
        managerService.addCompany("Hertz");
        verify(repository).addCompany("Hertz");
    }

    @Test
    void getCarsForCompany_returnsCarsFromRepository() {
        Company company = new Company(1, "Hertz");
        List<Car> cars = List.of(new Car(1, "Tesla", 1), new Car(2, "BMW", 1));
        when(repository.listCarsByCompanyId(1)).thenReturn(cars);
        List<Car> result = managerService.getCarsForCompany(company);
        assertEquals(2, result.size());
        assertEquals("Tesla", result.get(0).getName());
    }

    @Test
    void addCar_delegatesToRepository() {
        Company company = new Company(1, "Hertz");
        managerService.addCar("Tesla", company);
        verify(repository).addCompanyCar("Tesla", 1);
    }
}
