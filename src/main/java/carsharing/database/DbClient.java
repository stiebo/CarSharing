package carsharing.database;

import carsharing.dao.exception.ExecuteSQLException;
import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DbClient {
    private final String JDBC_DRIVER = "org.h2.Driver";
    private final String DB_URL;

    public DbClient(String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = "anything";
        }
        DB_URL = "jdbc:h2:" + System.getProperty("user.dir") + File.separator + filename;
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    private <T> T selectSingle(String sql, Function<ResultSet, T> mapper, Object... params) throws ExecuteSQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                T result = mapper.apply(resultSet);

                if (resultSet.next()) {
                    throw new IllegalStateException("Query returned more than one object");
                }

                return result;
            } else {
                return null; // Or throw an exception if necessary
            }
        } catch (SQLException | ClassNotFoundException se) {
            throw new ExecuteSQLException(se);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                throw new ExecuteSQLException(se);
            }
        }
    }

    public Car selectCar(String sql, Object... params) throws ExecuteSQLException {
        return selectSingle(sql, resultSet -> {
            try {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int companyId = resultSet.getInt("company_id");
                return new Car(id, name, companyId);
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping Car object", e);
            }
        }, params);
    }

    public Company selectCompany(String sql, Object... params) throws ExecuteSQLException {
        return selectSingle(sql, resultSet -> {
            try {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                return new Company(id, name);
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping Company object", e);
            }
        }, params);
    }

    private <T> List<T> selectList(String sql, Function<ResultSet, T> mapper, Object... params) throws ExecuteSQLException {
        List<T> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                result.add(mapper.apply(resultSet));
            }
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new ExecuteSQLException(se);
        } catch (Exception e) {
            throw new ExecuteSQLException(e);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                throw new ExecuteSQLException(se);
            }
        }
        return result;
    }

    public List<Car> listCarsByCompanyId(String sql, Object... params) throws ExecuteSQLException {
        return selectList(sql, resultSet -> {
            try {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int company_id = resultSet.getInt("company_id");
                return new Car(id, name, company_id);
            }
            catch (SQLException e) {
                throw new ExecuteSQLException(e);
            }
        }, params);
    }

    public List<Customer> listCustomers(String sql, Object... params) throws ExecuteSQLException {
        return selectList(sql, resultSet -> {
            try {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer rented_car_id = resultSet.getObject("rented_car_id", Integer.class);
                return new Customer(id, name, rented_car_id);
            }
            catch (SQLException e) {
                throw new ExecuteSQLException(e);
            }
        }, params);
    }

    public List<Company> listCompanies(String sql, Object... params) throws ExecuteSQLException {
        return selectList(sql, resultSet -> {
            try {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                return new Company(id, name);
            }
            catch (SQLException e) {
                throw new ExecuteSQLException(e);
            }
        }, params);
    }


    public void run(String sql, Object... params) throws ExecuteSQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            //STEP 2: Open a connection
            conn = DriverManager.getConnection(DB_URL);
            // For Hyperskill testing requirements
            conn.setAutoCommit(true);
            //STEP 3: Execute a query
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            stmt.executeUpdate();
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            throw new ExecuteSQLException(se);
        } catch (Exception e) {
            //Handle errors for Class.forName
            throw new ExecuteSQLException(e);
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                throw new ExecuteSQLException(se);
            } //end finally try
        } //end try
    }


}
