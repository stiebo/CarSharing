package carsharing.business;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.userinterface.*;
import carsharing.repository.Repository;

import java.util.List;

public class ManagerService {
    private Repository repository;

    public ManagerService(Repository repository) {
        this.repository = repository;
    }

    public void managerLogin() {
        new ManagerMenu(this).run();
    }

    public void listCompany() {
        List<Company> companies = repository.listCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return;
        }
        Menu chooseCompanyMenu = new Menu("Choose a company:");
        for (int i = 0; i < companies.size(); i++) {
            Company company = companies.get(i);
            chooseCompanyMenu.addMenuEntry(new MenuEntry(i + 1, company.getName(), () -> {
                this.showCompanyMenu(company);
                chooseCompanyMenu.exit();
            }));
        }
        chooseCompanyMenu.addMenuBack();
        chooseCompanyMenu.run();
    }

    public void createCompany() {
        String companyName = UserInput.getInput("Enter the company name:" + System.lineSeparator());
        repository.addCompany(companyName);
        System.out.println("The company was created!");
    }

    public void showCompanyMenu(Company company) {
        new CompanyMenu(this, company).run();
    }

    public void showCompanyCars(Company company) {
        List<Car> cars = repository.listCarsByCompanyId(company.getId());
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
            return;
        }
        System.out.println("Car list:");
        for (int i = 0; i < cars.size(); i++) {
            System.out.printf("%d. %s%n", i+1, cars.get(i).getName());
        }
    }

    public void createCompanyCar(Company company) {
        String carName = UserInput.getInput("Enter the car name:" + System.lineSeparator());
        repository.addCompanyCar(carName, company.getId());
        System.out.println("The car was added!");
    }

}
