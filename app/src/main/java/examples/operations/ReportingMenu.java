package examples.operations;

import examples.service.ReportingService;
import examples.service.iservice.IReportingService;

import java.util.Scanner;

public class ReportingMenu {

    public static void start() {

        Scanner sc = new Scanner(System.in);

        IReportingService service = new ReportingService();

        while (true) {

            System.out.println("\n===== REPORTS & ANALYTICS =====");
            System.out.println("1. Daily Booking Report");
            System.out.println("2. Revenue Report (Date Range)");
            System.out.println("3. Booking Trends by Route");
            System.out.println("4. Booking Trends by Airline");
            System.out.println("5. Cancellation / Payment Success Rates");
            System.out.println("6. Airline Performance Comparison");
            System.out.println("7. Peak Booking Periods");
            System.out.println("8. Seat Utilization Report");
            System.out.println("9. Passenger Demographics");
            System.out.println("10. Repeat Customers & Lifetime Value");
            System.out.println("11. Passenger Preference Report");
            System.out.println("12. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.dailyBookingReport();
                case 2 -> service.revenueReport();
                case 3 -> service.bookingTrendsByRoute();
                case 4 -> service.bookingTrendsByAirline();
                case 5 -> service.cancellationAndPaymentRates();
                case 6 -> service.airlinePerformanceComparison();
                case 7 -> service.peakBookingPeriods();
                case 8 -> service.seatUtilizationReport();
                case 9 -> service.passengerDemographics();
                case 10 -> service.repeatCustomersAndLTV();
                case 11 -> service.passengerPreferenceReport();
                case 12 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}