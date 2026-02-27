package carsharing;

import carsharing.database.DbClient;
import carsharing.repository.Repository;
import carsharing.userinterface.DesktopApp;

import javax.swing.SwingUtilities;

public class Main {

    private static String parseArgs(String[] args) {
        if ((args.length == 2) && (args[0].equals("-databaseFileName"))) {
            return args[1];
        }
        return null;
    }

    public static void main(String[] args) {
        String filename = parseArgs(args);
        DbClient dbClient = new DbClient(filename);
        Repository repository = new Repository(dbClient);
        SwingUtilities.invokeLater(() -> new DesktopApp(repository).run());
    }
}
