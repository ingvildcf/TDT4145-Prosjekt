import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // Creating a scanner to get user input
        Scanner scanner = new Scanner(System.in);

        // Creating a UserInterface, which gives the user a menu for selecting use case
        UserInterface systemUser = new UserInterface(scanner);
        scanner.close();
    }
}
