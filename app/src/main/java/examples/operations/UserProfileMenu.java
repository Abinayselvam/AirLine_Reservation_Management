package examples.operations;

import examples.service.iservice.IUserProfileService;
import examples.service.UserProfileService;
import examples.util.SessionManager;

import java.util.Scanner;

public class UserProfileMenu {

    public static void start() {

        Scanner scanner = new Scanner(System.in);

        IUserProfileService profileService =
                new UserProfileService();

        while (true) {

            System.out.println("\n===== PASSENGER DASHBOARD =====");

            System.out.println("1. Add Profile");
            System.out.println("2. View Profile");
            System.out.println("3. Update Profile");
            System.out.println("4. Search Flights");
            System.out.println("5. Logout");

            System.out.print("Enter Choice : ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1 -> profileService.addProfile();

                case 2 -> profileService.viewProfile();

                case 3 -> profileService.updateProfile();

                case 4 -> FlightMenu.start();

                case 5 -> {

                    SessionManager.logout();

                    System.out.println("Logged Out Successfully");

                    return;
                }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}
