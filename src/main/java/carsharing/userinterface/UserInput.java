package carsharing.userinterface;

import java.util.Scanner;

public class UserInput {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getInput (String text) {
        System.out.print(text);
        return scanner.nextLine();
    }

}
