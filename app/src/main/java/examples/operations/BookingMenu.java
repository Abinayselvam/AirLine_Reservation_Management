package examples.operations;

import examples.service.BookingService;
import examples.service.iservice.IBookingService;
import java.util.Scanner;

public class BookingMenu {

    public static void start() {

        Scanner sc = new Scanner(System.in);

        IBookingService service = new BookingService();

        while (true) {

            System.out.println("\n===== BOOKINGS =====");
            System.out.println("1. Book a Flight");
            System.out.println("2. View Booking by PNR");
            System.out.println("3. View Booking by Email/Phone");
            System.out.println("4. View Booking by E-Ticket");
            System.out.println("5. My Booking History");
            System.out.println("6. Request Refund");
            System.out.println("7. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.createBooking();
                case 2 -> service.viewBookingByPNR();
                case 3 -> service.viewBookingByContact();
                case 4 -> service.viewBookingByETicket();
                case 5 -> service.viewMyBookings();
                case 6 -> new examples.service.PaymentService().processStandaloneRefund();
                case 7 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }
}