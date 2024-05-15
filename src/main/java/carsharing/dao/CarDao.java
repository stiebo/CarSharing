package carsharing.dao;

import carsharing.domain.Car;

import java.util.List;

public interface CarDao {
    List<Car> findByCompany_Id(int id);
    List<Car> findAvailableByCompany_Id(int id);
    Car findById(int id);
    void create(String carName, int companyId);
    void update(Car car);
    //void deleteById(int id);
}
