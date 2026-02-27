package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.repository.Repository;

import java.util.List;

public class ManagerService {
    private final Repository repository;

    public ManagerService(Repository repository) {
        this.repository = repository;
    }

    public List<Company> getCompanies() {
        return repository.listCompanies();
    }

    public void addCompany(String name) {
        repository.addCompany(name);
    }

    public List<Car> getCarsForCompany(Company company) {
        return repository.listCarsByCompanyId(company.getId());
    }

    public void addCar(String name, Company company) {
        repository.addCompanyCar(name, company.getId());
    }
}

