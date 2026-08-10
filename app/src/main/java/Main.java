import examples.operations.UserMenu;
import examples.exception.AirlineSystemException;
import examples.util.ExceptionLogger;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the AirLine Reservation Management System");

        try {

            UserMenu.start();

        } catch (AirlineSystemException e) {

            ExceptionLogger.printFriendly(e);

            System.out.println("The application encountered an error and needs to close. Please restart.");

        } catch (Exception e) {

            System.out.println("An unexpected error occurred. Please restart the application.");

            ExceptionLogger.log(e);
        }
    }
}
