package carsharing.userinterface;

import carsharing.business.ManagerService;
import carsharing.domain.Company;

public class CompanyMenu extends Menu {
    public CompanyMenu(ManagerService managerService, Company company) {
        super("'%s' company:".formatted(company.getName()));
        menuEntries.add(new MenuEntry(1, "Car list", () -> {
            managerService.showCompanyCars(company);} ));
        menuEntries.add(new MenuEntry(2, "Create a car", () -> {
            managerService.createCompanyCar(company);}));
        menuEntries.add(new MenuEntry(0, "Back", this::exit));
    }
}
