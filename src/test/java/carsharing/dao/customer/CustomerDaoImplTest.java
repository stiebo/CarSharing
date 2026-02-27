package carsharing.dao.customer;

import carsharing.dao.impl.CarDaoImpl;
import carsharing.dao.impl.CompanyDaoImpl;
import carsharing.dao.impl.CustomerDaoImpl;
import carsharing.database.DbClient;
import carsharing.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDaoImplTest {

    private DbClient dbClient;
    private CustomerDaoImpl customerDao;

    @BeforeEach
    void setUp() {
        dbClient = new DbClient("mem:testCustomerDb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        new CompanyDaoImpl(dbClient);
        new CarDaoImpl(dbClient);
        customerDao = new CustomerDaoImpl(dbClient);
    }

    @Test
    void findAll_emptyDatabase_returnsEmptyList() {
        assertTrue(customerDao.findAll().isEmpty());
    }

    @Test
    void add_thenFindAll_returnsCustomer() {
        customerDao.add("Alice");
        List<Customer> result = customerDao.findAll();
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertNull(result.get(0).getRentedCarId());
    }

    @Test
    void findById_existingCustomer_returnsCustomer() {
        customerDao.add("Bob");
        int id = customerDao.findAll().get(0).getId();
        Customer found = customerDao.findById(id);
        assertNotNull(found);
        assertEquals("Bob", found.getName());
    }

    @Test
    void findById_nonExistentId_returnsNull() {
        assertNull(customerDao.findById(9999));
    }

    @Test
    void update_rentedCarIdNull_updatesCorrectly() {
        customerDao.add("Carol");
        Customer carol = customerDao.findAll().get(0);
        carol.setRentedCarId(null);
        customerDao.update(carol);
        Customer updated = customerDao.findById(carol.getId());
        assertNull(updated.getRentedCarId());
    }

    @Test
    void update_setRentedCarId_persistsToDb() {
        CompanyDaoImpl companyDao = new CompanyDaoImpl(dbClient);
        CarDaoImpl carDao = new CarDaoImpl(dbClient);
        companyDao.add("TestCo");
        int cId = companyDao.findAll().get(0).getId();
        carDao.create("Toyota", cId);
        int carId = carDao.findByCompanyId(cId).get(0).getId();

        customerDao.add("Dave");
        Customer dave = customerDao.findAll().get(0);
        dave.setRentedCarId(carId);
        customerDao.update(dave);

        Customer updated = customerDao.findById(dave.getId());
        assertEquals(carId, updated.getRentedCarId());
    }
}
