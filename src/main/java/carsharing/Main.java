package carsharing;


import carsharing.business.CustomerService;
import carsharing.business.ManagerService;
import carsharing.database.DbClient;
import carsharing.userinterface.MainMenu;
import carsharing.repository.Repository;

public class Main {

    private static String parseArgs(String[] args) {
        if ((args.length == 2) && (args[0].equals("-databaseFileName"))) {
            return args[1];
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        String filename = parseArgs(args);
        DbClient dbClient = new DbClient(filename);
        Repository repository = new Repository(dbClient);
        new MainMenu(new ManagerService(repository), new CustomerService(repository)).run();
    }
}