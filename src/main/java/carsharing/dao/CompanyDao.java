package carsharing.dao;

import carsharing.domain.Company;

import java.util.List;

public interface CompanyDao {
    List<Company> findAll();
    Company findById(int id);
    void add(String companyName);
}
