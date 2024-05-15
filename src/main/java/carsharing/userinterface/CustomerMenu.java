package carsharing.userinterface;

import carsharing.business.CustomerService;
import carsharing.domain.Customer;

public class CustomerMenu extends Menu {
    public CustomerMenu(CustomerService customerService, Customer customer) {
        //super("'%s' company:".formatted(customer.getName()));
        super("");
        menuEntries.add(new MenuEntry(1, "Rent a car", () -> {
            customerService.rentCar(customer);
        }));
        menuEntries.add(new MenuEntry(2, "Return a rented car", () -> {
            customerService.returnCar(customer);
        }));
        menuEntries.add(new MenuEntry(3, "My rented car", () -> {
            customerService.showRentedCar(customer);
        }));
        menuEntries.add(new MenuEntry(0, "Back", this::exit));
    }
}
