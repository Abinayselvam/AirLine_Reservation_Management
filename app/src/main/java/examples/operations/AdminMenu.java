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

                    System.out.println("\n===== SYSTEM CACHE / LOG STATS =====");

                    System.out.println("FlightManager cached flights : " +
                            examples.manager.FlightManager.getInstance().cacheSize());

                    System.out.println("BookingManager cached bookings : " +
                            examples.manager.BookingManager.getInstance().cacheSize());

                    System.out.println("PaymentManager transaction log size : " +
                            examples.manager.PaymentManager.getInstance().logSize());
                }

                case 8 -> {

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