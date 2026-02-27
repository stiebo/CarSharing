package carsharing.dao.car;

import carsharing.dao.impl.CarDaoImpl;
import carsharing.dao.impl.CompanyDaoImpl;
import carsharing.dao.impl.CustomerDaoImpl;
import carsharing.database.DbClient;
import carsharing.domain.Car;
import carsharing.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarDaoImplTest {

    private DbClient dbClient;
    private CarDaoImpl carDao;
    private int companyId;

    @BeforeEach
    void setUp() {
        dbClient = new DbClient("mem:testCarDb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        new CompanyDaoImpl(dbClient).add("TestCompany");
        carDao = new CarDaoImpl(dbClient);
        new CustomerDaoImpl(dbClient);
        companyId = new CompanyDaoImpl(dbClient).findAll().get(0).getId();
    }

    @Test
    void create_thenFindByCompanyId_returnsCar() {
        carDao.create("Volvo", companyId);
        List<Car> cars = carDao.findByCompanyId(companyId);
        assertEquals(1, cars.size());
        assertEquals("Volvo", cars.get(0).getName());
        assertEquals(companyId, cars.get(0).getCompanyId());
    }

    @Test
    void findByCompanyId_noMatchingCompany_returnsEmpty() {
        carDao.create("BMW", companyId);
        List<Car> cars = carDao.findByCompanyId(companyId + 999);
        assertTrue(cars.isEmpty());
    }

    @Test
    void findById_existingCar_returnsCar() {
        carDao.create("Toyota", companyId);
        int id = carDao.findByCompanyId(companyId).get(0).getId();
        Car found = carDao.findById(id);
        assertNotNull(found);
        assertEquals("Toyota", found.getName());
    }

    @Test
    void findById_nonExistent_returnsNull() {
        assertNull(carDao.findById(9999));
    }

    @Test
    void findAvailableByCompanyId_noRentals_returnsAllCars() {
        carDao.create("Ford", companyId);
        carDao.create("Honda", companyId);
        List<Car> available = carDao.findAvailableByCompanyId(companyId);
        assertEquals(2, available.size());
    }

    @Test
    void findAvailableByCompanyId_withRental_excludesRentedCar() {
        carDao.create("Mazda", companyId);
        int carId = carDao.findByCompanyId(companyId).get(0).getId();

        CustomerDaoImpl customerDao = new CustomerDaoImpl(dbClient);
        customerDao.add("Renter");
        Customer renter = customerDao.findAll().get(0);
        renter.setRentedCarId(carId);
        customerDao.update(renter);

        List<Car> available = carDao.findAvailableByCompanyId(companyId);
        assertTrue(available.isEmpty());
    }
}
