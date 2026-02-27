package carsharing.dao;

import carsharing.domain.Customer;

import java.util.List;

public interface CustomerDao {
    List<Customer> findAll();
    Customer findById(int id);
    void add(String customerName);
    void update(Customer customer);
}
