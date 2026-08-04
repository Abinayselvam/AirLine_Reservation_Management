package examples.operations;

import examples.enums.Permission;
import examples.model.User;
import examples.util.AccessValidator;
import examples.util.SessionManager;
import java.util.Scanner;

public class AirlineStaffMenu {

    public static void start() {

        Scanner sc =
                new Scanner(System.in);

        User user =
                SessionManager.getLoggedInUser();

        user.showDashboard();

        while (true) {

            System.out.print("Choice : ");

            int choice =
                    Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> FlightManagementMenu.start();

                case 2 -> {

                    if (AccessValidator.validate(
                            Permission.VIEW_ALL_BOOKINGS)) {

                        System.out.println(
                                "View bookings coming in UC 4");
                    }
                }

                case 3 -> {

                    SessionManager.logout();
                    return;
                }

                default ->
                        System.out.println(
                                "Invalid Choice");
            }
        }
    }
}