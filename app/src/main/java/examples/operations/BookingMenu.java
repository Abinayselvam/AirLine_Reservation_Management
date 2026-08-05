package examples.operations;

import examples.service.BookingCancellationService;
import examples.service.BookingService;
import examples.service.iservice.IBookingCancellationService;
import examples.service.iservice.IBookingModificationService;
import examples.service.BookingModificationService;
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
            System.out.println("7. Modify Booking");
            System.out.println("8. Cancel Booking");
            System.out.println("9. Online Check-In");
            System.out.println("10. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.createBooking();
                case 2 -> service.viewBookingByPNR();
                case 3 -> service.viewBookingByContact();
                case 4 -> service.viewBookingByETicket();
                case 5 -> service.viewMyBookings();
                case 6 -> new examples.service.PaymentService().processStandaloneRefund();
                case 7 -> modifyMenu();
                case 8 -> cancelMenu();
                case 9 -> CheckInMenu.start();
                case 10 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice");
            }
        }

    }
    private static void modifyMenu() {

        Scanner sc = new Scanner(System.in);

        IBookingModificationService modService =
                new BookingModificationService();

        System.out.println("\n1. Change Flight  2. Modify Passenger Details  3. Change Seat  4. Back");

        System.out.print("Choice : ");

        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {

            case 1 -> modService.changeFlight();
            case 2 -> modService.modifyPassengerDetails();
            case 3 -> modService.changeSeat();
            default -> {}
        }
    }
    private static void cancelMenu() {

        Scanner sc = new Scanner(System.in);

       IBookingCancellationService cancelService =
                new BookingCancellationService();

        System.out.println("\n1. Cancel Entire Booking  2. Cancel Specific Passengers  3. Back");

        System.out.print("Choice : ");

        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {

            case 1 -> cancelService.cancelFullBooking();
            case 2 -> cancelService.cancelPartialBooking();
            default -> {}
        }
    }
}