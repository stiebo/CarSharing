package carsharing.userinterface;

import carsharing.business.ManagerService;

public class ManagerMenu extends Menu {
    public ManagerMenu(ManagerService managerService) {
        super("");
        menuEntries.add(new MenuEntry(1, "Company list", managerService::listCompany));
        menuEntries.add(new MenuEntry(2, "Create a company", managerService::createCompany));
        menuEntries.add(new MenuEntry(0, "Back", this::exit));
    }
}
