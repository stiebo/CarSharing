package carsharing.userinterface;

import carsharing.business.CustomerService;
import carsharing.business.ManagerService;

public class MainMenu extends Menu {
    public MainMenu(ManagerService managerService, CustomerService customerService) {
        super("");
        menuEntries.add(new MenuEntry(1, "Log in as a manager", managerService::managerLogin));
        menuEntries.add(new MenuEntry(2, "Log in as a customer", customerService::customerLogin));
        menuEntries.add(new MenuEntry(3, "Create a customer", customerService::createCustomer));
        menuEntries.add(new MenuEntry(0, "Exit", this::exit));
    }
}
