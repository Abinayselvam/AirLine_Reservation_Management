package examples.operations;

import examples.enums.BookingPriority;
import examples.enums.Permission;
import examples.model.BookingRequest;
import examples.service.BookingService;
import examples.service.PriorityBookingQueueService;
import examples.service.iservice.IPriorityBookingQueueService;
import examples.util.AccessValidator;
import examples.util.SessionManager;
import java.util.Scanner;
public class PriorityBookingMenu {

    private static final double EXPRESS_FEE = 750;

    // Shared instance so requests submitted by passengers stay visible
    // to whichever staff/admin session processes the queue next.
    private static final IPriorityBookingQueueService queueService = new PriorityBookingQueueService();

    public static void start() {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== PRIORITY BOOKING QUEUE =====");
            System.out.println("1. Submit Booking Request");
            System.out.println("2. Process Next Request (Staff/Admin)");
            System.out.println("3. View Queue Report");
            System.out.println("4. Back");

            System.out.print("Choice : ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> submitRequest(sc);

                case 2 -> {

                    if (AccessValidator.validate(Permission.VIEW_ALL_BOOKINGS)) {
                        processNext();
                    }
                }

                case 3 -> queueService.printReport();

                case 4 -> { return; }

                default -> System.out.println("Invalid Choice");
            }
        }
    }

    private static void submitRequest(Scanner sc) {

        var user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Login Required");

            return;
        }

        System.out.print("Flight ID : ");

        int flightId = Integer.parseInt(sc.nextLine());

        System.out.print("Number of Passengers : ");

        int count = Integer.parseInt(sc.nextLine());

        System.out.print("Opt for Express Processing (+Rs." + EXPRESS_FEE + ") ? (y/n) : ");

        boolean express = sc.nextLine().equalsIgnoreCase("y");

        BookingRequest request = queueService.submit(
                user.getId(), flightId, count,
                express ? BookingPriority.EXPRESS : BookingPriority.REGULAR);

        System.out.println("Request #" + request.getRequestId() + " submitted as " +
                request.getPriority() + ". Current queue size : " + queueService.queueSize());
    }

    private static void processNext() {

        BookingRequest request = queueService.processNext();

        if (request == null) {

            System.out.println("Queue is empty");

            return;
        }

        System.out.println("\nProcessing Request #" + request.getRequestId() +
                " (" + request.getPriority() + ") - User " + request.getUserId() +
                ", Flight " + request.getFlightId() + ", " +
                request.getPassengerCount() + " passenger(s)");

        System.out.println("Handing off to the standard booking flow to complete it:");

        new BookingService().createBooking();
    }
}