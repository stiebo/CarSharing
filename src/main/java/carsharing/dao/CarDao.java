package carsharing.dao;

import carsharing.domain.Car;

import java.util.List;

public interface CarDao {
    List<Car> findByCompanyId(int id);
    List<Car> findAvailableByCompanyId(int id);
    Car findById(int id);
    void create(String carName, int companyId);
}
