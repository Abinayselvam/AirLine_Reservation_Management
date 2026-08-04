package examples.operations;

import examples.service.AirportService;
import examples.service.iservice.IAirportService;
import examples.util.SessionManager;

import java.util.Scanner;

public class AirportMenu {

    public static void start() {

        Scanner sc = new Scanner(System.in);

        IAirportService service = new AirportService();

        boolean isAdmin = SessionManager.getLoggedInUser() != null
                && SessionManager.getLoggedInUser().getRole() == examples.enums.Role.ADMIN;

        while (true) {

            System.out.println("\n===== AIRPORTS =====");

            if (isAdmin) {
                System.out.println("1. Add Airport");
                System.out.println("2. Update Airport");
                System.out.println("3. Activate/Deactivate Airport");
            }

            System.out.println("4. Search by Code");
            System.out.println("5. Search by City");
            System.out.println("6. Search by Name");
            System.out.println("7. List by Country");
            System.out.println("8. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.addAirport();
                case 2 -> service.updateAirport();
                case 3 -> service.toggleActive();
                case 4 -> service.searchByCode();
                case 5 -> service.searchByCity();
                case 6 -> service.searchByName();
                case 7 -> service.listByCountry();
                case 8 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}