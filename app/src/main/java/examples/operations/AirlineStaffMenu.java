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

                        System.out.println("\n1. Lookup by PNR  2. Lookup by Email/Phone  3. Lookup by E-Ticket");

                        System.out.print("Choice : ");

                        int lookupChoice = Integer.parseInt(sc.nextLine());

                        var bookingService = new examples.service.BookingService();

                        switch (lookupChoice) {
                            case 1 -> bookingService.viewBookingByPNR();
                            case 2 -> bookingService.viewBookingByContact();
                            case 3 -> bookingService.viewBookingByETicket();
                            default -> System.out.println("Invalid Choice");
                        }
                    }
                }
                case 3 -> PriorityBookingMenu.start();
                case 4 -> {

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