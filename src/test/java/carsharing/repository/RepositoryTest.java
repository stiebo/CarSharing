package carsharing.repository;

import carsharing.database.DbClient;
import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private Repository repository;

    @BeforeEach
    void setUp() {
        DbClient dbClient = new DbClient("mem:testRepo_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        repository = new Repository(dbClient);
    }

    // ── Company ───────────────────────────────────────────────────────────

    @Test
    void listCompanies_empty_returnsEmptyList() {
        assertTrue(repository.listCompanies().isEmpty());
    }

    @Test
    void addCompany_thenListCompanies_returnsCompany() {
        repository.addCompany("Hertz");
        List<Company> result = repository.listCompanies();
        assertEquals(1, result.size());
        assertEquals("Hertz", result.get(0).getName());
    }

    @Test
    void findCompanyById_existingId_returnsCompany() {
        repository.addCompany("Avis");
        int id = repository.listCompanies().get(0).getId();
        Company found = repository.findCompanyById(id);
        assertNotNull(found);
        assertEquals("Avis", found.getName());
    }

    @Test
    void findCompanyById_nonExistentId_returnsNull() {
        assertNull(repository.findCompanyById(9999));
    }

    @Test
    void addCompany_multipleCompanies_allPresent() {
        repository.addCompany("Alpha");
        repository.addCompany("Beta");
        repository.addCompany("Gamma");
        assertEquals(3, repository.listCompanies().size());
    }

    // ── Car ───────────────────────────────────────────────────────────────

    @Test
    void addCompanyCar_thenListCarsByCompanyId_returnsCar() {
        repository.addCompany("CarCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("Tesla", companyId);
        List<Car> cars = repository.listCarsByCompanyId(companyId);
        assertEquals(1, cars.size());
        assertEquals("Tesla", cars.get(0).getName());
    }

    @Test
    void listCarsByCompanyId_noMatchingCompany_returnsEmpty() {
        assertTrue(repository.listCarsByCompanyId(9999).isEmpty());
    }

    @Test
    void findCarById_existingCar_returnsCar() {
        repository.addCompany("FindCarCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("Honda", companyId);
        int carId = repository.listCarsByCompanyId(companyId).get(0).getId();
        Car car = repository.findCarById(carId);
        assertNotNull(car);
        assertEquals("Honda", car.getName());
    }

    @Test
    void findCarById_nonExistentId_returnsNull() {
        assertNull(repository.findCarById(9999));
    }

    @Test
    void listAvailableCarsByCompanyId_noRentals_returnsAllCars() {
        repository.addCompany("AvailCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("Ford", companyId);
        repository.addCompanyCar("Mazda", companyId);
        assertEquals(2, repository.listAvailableCarsByCompanyId(companyId).size());
    }

    @Test
    void listAvailableCarsByCompanyId_withRental_excludesRentedCar() {
        repository.addCompany("RentCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("Rented", companyId);
        int carId = repository.listCarsByCompanyId(companyId).get(0).getId();

        repository.addCustomer("Driver");
        Customer driver = repository.listCustomers().get(0);
        driver.setRentedCarId(carId);
        repository.updateCustomer(driver);

        assertTrue(repository.listAvailableCarsByCompanyId(companyId).isEmpty());
    }

    // ── Customer ──────────────────────────────────────────────────────────

    @Test
    void listCustomers_empty_returnsEmptyList() {
        assertTrue(repository.listCustomers().isEmpty());
    }

    @Test
    void addCustomer_thenListCustomers_returnsCustomer() {
        repository.addCustomer("Alice");
        List<Customer> result = repository.listCustomers();
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertNull(result.get(0).getRentedCarId());
    }

    @Test
    void findCustomerById_existingId_returnsCustomer() {
        repository.addCustomer("Bob");
        int id = repository.listCustomers().get(0).getId();
        Customer found = repository.findCustomerById(id);
        assertNotNull(found);
        assertEquals("Bob", found.getName());
    }

    @Test
    void findCustomerById_nonExistentId_returnsNull() {
        assertNull(repository.findCustomerById(9999));
    }

    @Test
    void updateCustomer_changesRentedCarId_persists() {
        repository.addCompany("UpdateCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("BMW", companyId);
        int carId = repository.listCarsByCompanyId(companyId).get(0).getId();

        repository.addCustomer("Carol");
        Customer carol = repository.listCustomers().get(0);
        carol.setRentedCarId(carId);
        repository.updateCustomer(carol);

        assertEquals(carId, repository.findCustomerById(carol.getId()).getRentedCarId());
    }

    @Test
    void updateCustomer_clearRentedCar_persists() {
        repository.addCompany("ClearCo");
        int companyId = repository.listCompanies().get(0).getId();
        repository.addCompanyCar("Kia", companyId);
        int carId = repository.listCarsByCompanyId(companyId).get(0).getId();

        repository.addCustomer("Dave");
        Customer dave = repository.listCustomers().get(0);
        dave.setRentedCarId(carId);
        repository.updateCustomer(dave);

        dave.setRentedCarId(null);
        repository.updateCustomer(dave);

        assertNull(repository.findCustomerById(dave.getId()).getRentedCarId());
    }
}
