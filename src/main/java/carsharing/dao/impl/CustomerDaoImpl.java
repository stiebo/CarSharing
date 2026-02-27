package carsharing.dao.impl;

import carsharing.dao.CustomerDao;
import carsharing.database.DbClient;
import carsharing.domain.Customer;

import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    private final DbClient dbClient;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS CUSTOMER " +
            "(id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) UNIQUE NOT NULL, " +
            "rented_car_id INTEGER, " +
            "FOREIGN KEY (rented_car_id) REFERENCES CAR(id))";
    private static final String SELECT_ALL_ORDER_BY_ID = "SELECT * FROM CUSTOMER ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM CUSTOMER WHERE id = ?";
    private static final String ADD = "INSERT INTO CUSTOMER (NAME) VALUES (?);";
    private static final String UPDATE = "UPDATE CUSTOMER SET name=?, rented_car_id=? WHERE id=?";

    public CustomerDaoImpl(DbClient dbClient) {
        this.dbClient = dbClient;
        dbClient.run(CREATE_TABLE);
    }

    @Override
    public List<Customer> findAll() {
        return dbClient.listCustomers(SELECT_ALL_ORDER_BY_ID);
    }

    @Override
    public Customer findById(int id) {
        return dbClient.selectCustomer(SELECT_BY_ID, id);
    }

    @Override
    public void add(String customerName) {
        dbClient.run(ADD, customerName);
    }

    @Override
    public void update(Customer customer) {
        dbClient.run(UPDATE, customer.getName(), customer.getRentedCarId(), customer.getId());
    }
}
