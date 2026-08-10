package examples.operations;

import examples.service.FlightManagementService;
import examples.service.iservice.IFlightManagementService;

import java.util.Scanner;

public class FlightManagementMenu {

    public static void start() {

        Scanner sc = new Scanner(System.in);

        IFlightManagementService service = new FlightManagementService();

        while (true) {

            System.out.println("\n===== FLIGHT MANAGEMENT =====");
            System.out.println("1. Create Flight");
            System.out.println("2. Update Flight Schedule");
            System.out.println("3. Update Fare");
            System.out.println("4. Update Aircraft Type");
            System.out.println("5. Update Flight Status");
            System.out.println("6. Search / Filter Flights");
            System.out.println("7. View Occupancy Rates");
            System.out.println("8. Generate Flight Report");
            System.out.println("9. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.createFlight();
                case 2 -> service.updateFlightSchedule();
                case 3 -> service.updateFareStructure();
                case 4 -> service.updateAircraftType();
                case 5 -> service.updateFlightStatus();
                case 6 -> service.searchFlightsAdmin();
                case 7 -> service.viewOccupancyRates();
                case 8 -> service.generateFlightReport();
                case 9 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}
