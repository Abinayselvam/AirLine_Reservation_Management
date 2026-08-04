package examples.operations;

import examples.service.FlightService;
import examples.service.IFlightService;

import java.util.Scanner;

public class FlightMenu {

    public static void start() {

        Scanner sc = new Scanner(System.in);

        IFlightService service = new FlightService();

        while (true) {

            System.out.println("\n===== FLIGHT SEARCH =====");
            System.out.println("1. Search Flights");
            System.out.println("2. View Grouped by Airline");
            System.out.println("3. View Average Fare by Airline");
            System.out.println("4. View Cheapest Flight per Route");
            System.out.println("5. View Grouped by Price Range");
            System.out.println("6. View Grouped by Departure Time Slot");
            System.out.println("7. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.searchFlights();

                case 2 -> service.viewGroupedByAirline();

                case 3 -> service.viewAverageFareByAirline();

                case 4 -> service.viewCheapestFlightsByRoute();

                case 5 -> service.viewGroupedByPriceRange();

                case 6 -> service.viewGroupedByDepartureSlot();

                case 7 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}