package carsharing.dao.impl;

import carsharing.dao.CompanyDao;
import carsharing.database.DbClient;
import carsharing.domain.Company;

import java.util.List;

public class CompanyDaoImpl implements CompanyDao {

    private final DbClient dbClient;
    private static final String CREATE_DB = "CREATE TABLE IF NOT EXISTS COMPANY " +
            "(id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) UNIQUE NOT NULL)";
    private static final String SELECT_ALL_ORDER_BY_ID = "SELECT * FROM COMPANY ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM COMPANY WHERE id = ?";
    private static final String ADD = "INSERT INTO COMPANY (NAME) VALUES (?)";

    public CompanyDaoImpl(DbClient dbClient) {
        this.dbClient = dbClient;
        dbClient.run(CREATE_DB);
    }

    @Override
    public List<Company> findAll() {
        return dbClient.listCompanies(SELECT_ALL_ORDER_BY_ID);
    }

    @Override
    public Company findById(int id) {
        return dbClient.selectCompany(SELECT_BY_ID, id);
    }

    @Override
    public void add(String companyName) {
        dbClient.run(ADD, companyName);
    }
}
