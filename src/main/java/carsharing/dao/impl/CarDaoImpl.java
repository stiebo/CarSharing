package carsharing.dao.impl;

import carsharing.dao.CarDao;
import carsharing.database.DbClient;
import carsharing.domain.Car;

import java.util.List;

public class CarDaoImpl implements CarDao {

    private final DbClient dbClient;
    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS CAR " +
            "(id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "company_id INTEGER NOT NULL, " +
            "FOREIGN KEY (company_id) REFERENCES COMPANY(id))";
    private static final String ADD = "INSERT INTO CAR (name, company_id) VALUES (?, ?)";
    private static final String FIND_BY_COMPANY_ID_SORT_BY_ID = "SELECT * FROM CAR WHERE company_id = ? ORDER BY id";
    private static final String FIND_AVAILABLE_BY_COMPANY_ID_SORT_BY_ID = "SELECT CAR.* FROM CAR WHERE " +
            "CAR.company_id=? AND NOT EXISTS( SELECT 1 FROM Customer WHERE Customer.rented_car_id = Car.id)";
    private static final String FIND_BY_ID = "SELECT * FROM CAR WHERE id = ? ORDER BY id";
    private static final String UPDATE = "UPDATE CAR SET name = ?, company_id = ? WHERE id = ?";


    public CarDaoImpl(DbClient dbClient) {
        this.dbClient = dbClient;
        dbClient.run(CREATE_DB);
    }

    @Override
    public List<Car> findByCompany_Id(int id) {
        return dbClient.listCarsByCompanyId(FIND_BY_COMPANY_ID_SORT_BY_ID, id);
    }

    @Override
    public List<Car> findAvailableByCompany_Id(int id) {
        return dbClient.listCarsByCompanyId(FIND_AVAILABLE_BY_COMPANY_ID_SORT_BY_ID, id);
    }

    @Override
    public Car findById(int id) {
        return dbClient.selectCar(FIND_BY_ID, id);
    }

    @Override
    public void create(String carName, int companyId) {
        dbClient.run(ADD, carName, companyId);
    }

    @Override
    public void update(Car car) {
        dbClient.run(UPDATE, car.getName(), car.getCompanyId(), car.getId());
    }
}
