package examples.operations;

import examples.enums.Permission;
import examples.model.User;
import examples.util.AccessValidator;
import examples.util.SessionManager;

import java.util.Scanner;

public class AdminMenu {

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
                            Permission.MANAGE_ALL_USERS)) {

                        System.out.println(
                                "User management coming in a later UC");
                    }
                }

                case 3, 4 -> {

                    if (AccessValidator.validate(
                            Permission.VIEW_REPORTS)) {

                        System.out.println(
                                "Reports coming in UC 13");
                    }
                }

                case 5 -> AirportMenu.start();

                case 6 -> PriorityBookingMenu.start();

                case 7 -> {

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