package carsharing.database;

import carsharing.dao.impl.CarDaoImpl;
import carsharing.dao.impl.CompanyDaoImpl;
import carsharing.dao.impl.CustomerDaoImpl;
import carsharing.dao.exception.ExecuteSQLException;
import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DbClientTest {

    private DbClient dbClient;

    @BeforeEach
    void setUp() {
        dbClient = new DbClient("mem:testDbClient_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        // initialise required tables via the DAOs
        new CompanyDaoImpl(dbClient);
        new CarDaoImpl(dbClient);
        new CustomerDaoImpl(dbClient);
    }

    // ── constructor ────────────────────────────────────────────────────────

    @Test
    void constructor_nullFilename_doesNotThrow() {
        assertDoesNotThrow(() -> new DbClient(null));
    }

    @Test
    void constructor_emptyFilename_doesNotThrow() {
        assertDoesNotThrow(() -> new DbClient(""));
    }

    @Test
    void constructor_memPrefix_doesNotThrow() {
        assertDoesNotThrow(
                () -> new DbClient("mem:inlineDb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1"));
    }

    // ── run ───────────────────────────────────────────────────────────────

    @Test
    void run_insertAndSelectBack_works() throws ExecuteSQLException {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "RunTestCo");
        Company found = dbClient.selectCompany("SELECT * FROM COMPANY WHERE name = ?", "RunTestCo");
        assertNotNull(found);
        assertEquals("RunTestCo", found.getName());
    }

    // ── selectCompany ─────────────────────────────────────────────────────

    @Test
    void selectCompany_noMatch_returnsNull() throws ExecuteSQLException {
        Company result = dbClient.selectCompany(
                "SELECT * FROM COMPANY WHERE id = ?", 9999);
        assertNull(result);
    }

    @Test
    void selectCompany_singleMatch_returnsCompany() throws ExecuteSQLException {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "SingleCo");
        Company result = dbClient.selectCompany(
                "SELECT * FROM COMPANY WHERE name = ?", "SingleCo");
        assertNotNull(result);
        assertEquals("SingleCo", result.getName());
    }

    @Test
    void selectCompany_multipleRows_throwsIllegalStateException() {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "Alpha");
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "Beta");
        // SQL without a WHERE clause returns every row → triggers the guard
        assertThrows(IllegalStateException.class,
                () -> dbClient.selectCompany("SELECT * FROM COMPANY"));
    }

    // ── selectCar ─────────────────────────────────────────────────────────

    @Test
    void selectCar_noMatch_returnsNull() throws ExecuteSQLException {
        assertNull(dbClient.selectCar("SELECT * FROM CAR WHERE id = ?", 9999));
    }

    @Test
    void selectCar_singleMatch_returnsCar() throws ExecuteSQLException {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "CarCo");
        Company co = dbClient.selectCompany("SELECT * FROM COMPANY WHERE name = ?", "CarCo");
        dbClient.run("INSERT INTO CAR (name, company_id) VALUES (?, ?)", "Volvo", co.getId());
        Car car = dbClient.selectCar(
                "SELECT * FROM CAR WHERE name = ?", "Volvo");
        assertNotNull(car);
        assertEquals("Volvo", car.getName());
        assertEquals(co.getId(), car.getCompanyId());
    }

    @Test
    void selectCar_multipleRows_throwsIllegalStateException() {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "MultiCarCo");
        Company co = dbClient.selectCompany("SELECT * FROM COMPANY WHERE name = ?", "MultiCarCo");
        dbClient.run("INSERT INTO CAR (name, company_id) VALUES (?, ?)", "Car1", co.getId());
        dbClient.run("INSERT INTO CAR (name, company_id) VALUES (?, ?)", "Car2", co.getId());
        assertThrows(IllegalStateException.class,
                () -> dbClient.selectCar("SELECT * FROM CAR WHERE company_id = ?", co.getId()));
    }

    // ── selectCustomer ────────────────────────────────────────────────────

    @Test
    void selectCustomer_noMatch_returnsNull() throws ExecuteSQLException {
        assertNull(dbClient.selectCustomer("SELECT * FROM CUSTOMER WHERE id = ?", 9999));
    }

    @Test
    void selectCustomer_singleMatch_returnsCustomer() throws ExecuteSQLException {
        dbClient.run("INSERT INTO CUSTOMER (name) VALUES (?)", "Alice");
        Customer customer = dbClient.selectCustomer(
                "SELECT * FROM CUSTOMER WHERE name = ?", "Alice");
        assertNotNull(customer);
        assertEquals("Alice", customer.getName());
        assertNull(customer.getRentedCarId());
    }

    @Test
    void selectCustomer_multipleRows_throwsIllegalStateException() {
        dbClient.run("INSERT INTO CUSTOMER (name) VALUES (?)", "Bob");
        dbClient.run("INSERT INTO CUSTOMER (name) VALUES (?)", "Carol");
        assertThrows(IllegalStateException.class,
                () -> dbClient.selectCustomer("SELECT * FROM CUSTOMER"));
    }

    // ── listCompanies ─────────────────────────────────────────────────────

    @Test
    void listCompanies_empty_returnsEmptyList() throws ExecuteSQLException {
        List<Company> result = dbClient.listCompanies("SELECT * FROM COMPANY ORDER BY id");
        assertTrue(result.isEmpty());
    }

    @Test
    void listCompanies_withRecords_returnsList() throws ExecuteSQLException {
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "X");
        dbClient.run("INSERT INTO COMPANY (name) VALUES (?)", "Y");
        List<Company> result = dbClient.listCompanies("SELECT * FROM COMPANY ORDER BY id");
        assertEquals(2, result.size());
    }

    // ── listCarsByCompanyId ───────────────────────────────────────────────

    @Test
    void listCarsByCompanyId_noCars_returnsEmptyList() throws ExecuteSQLException {
        assertTrue(dbClient.listCarsByCompanyId(
                "SELECT * FROM CAR WHERE company_id = ? ORDER BY id", 1).isEmpty());
    }

    // ── listCustomers ─────────────────────────────────────────────────────

    @Test
    void listCustomers_empty_returnsEmptyList() throws ExecuteSQLException {
        assertTrue(dbClient.listCustomers("SELECT * FROM CUSTOMER ORDER BY id").isEmpty());
    }

    @Test
    void listCustomers_withRecords_returnsList() throws ExecuteSQLException {
        dbClient.run("INSERT INTO CUSTOMER (name) VALUES (?)", "Dave");
        List<Customer> result = dbClient.listCustomers("SELECT * FROM CUSTOMER ORDER BY id");
        assertEquals(1, result.size());
        assertEquals("Dave", result.get(0).getName());
    }
}
