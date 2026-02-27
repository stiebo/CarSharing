package carsharing.userinterface;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Menu {
    private final String name;
    protected ArrayList<MenuEntry> menuEntries;
    private boolean keepRunning;

    public Menu(String name) {
        this.name = name;
        this.menuEntries = new ArrayList<>();
        this.keepRunning = true;
    }

    public void addMenuEntry(MenuEntry menuEntry) {
        menuEntries.add(menuEntry);
    }

    public void addMenuBack() {
        menuEntries.add(new MenuEntry(0, "Back", this::exit));
    }

    public void run() {
        while (keepRunning) {
            printMenu();
            String input = UserInput.readLine();
            actionInput(input);
        }
    }

    private void printMenu() {
        System.out.println();
        if (!name.isEmpty()) {
            System.out.println(name);
        }
        System.out.print(
                menuEntries.stream()
                        .map(entry -> entry.getNumber() + ". " + entry.getText())
                        .collect(Collectors.joining(System.lineSeparator())) +
                System.lineSeparator());
    }

    protected void actionInput(String input) {
        Optional<MenuEntry> found = menuEntries.stream()
                .filter(entry -> String.valueOf(entry.getNumber()).equals(input))
                .findFirst();
        if (found.isPresent()) {
            found.get().execute();
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    public void exit() {
        keepRunning = false;
    }
}
