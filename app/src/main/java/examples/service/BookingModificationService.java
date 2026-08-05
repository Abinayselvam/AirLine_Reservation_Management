package examples.service;

import examples.enums.BookingStatus;
import examples.enums.MealPreference;
import examples.enums.SeatStatus;
import examples.model.*;
import examples.repository.*;
import examples.repository.irepository.IBookingPassengerRepository;
import examples.repository.irepository.IBookingRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.ISeatRepository;
import examples.service.iservice.IBookingModificationService;
import examples.util.ETicketGenerator;

import java.util.List;
import java.util.Scanner;

public class BookingModificationService implements IBookingModificationService {

    private static final double FLIGHT_CHANGE_FEE = 500;

    private static final double NAME_CORRECTION_FEE = 300;

    private final Scanner sc = new Scanner(System.in);

    private final IBookingRepository bookingRepository = new BookingRepository();

    private final IBookingPassengerRepository passengerRepository = new BookingPassengerRepository();

    private final IFlightRepository flightRepository = new FlightRepository();

    private final ISeatRepository seatRepository = new SeatRepository();

    // ---------- 6.1 Flight Change ----------

    @Override
    public void changeFlight() {

        Booking booking = fetchModifiableBooking();

        if (booking == null) {
            return;
        }

        Flight currentFlight = flightRepository.findById(booking.getFlightId());

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId());

        int passengerCount = passengers.size();

        System.out.println("Current Flight : " + currentFlight.getAirlineName() + " " +
                currentFlight.getFlightNumber() + " on " + currentFlight.getDepartureDate());

        System.out.print("New Departure Date (yyyy-MM-dd) : ");

        java.time.LocalDate newDate = java.time.LocalDate.parse(sc.nextLine());

        SearchCriteria criteria = new SearchCriteria();

        criteria.setSource(currentFlight.getSource());
        criteria.setDestination(currentFlight.getDestination());
        criteria.setDepartureDate(newDate);

        List<Flight> options = flightRepository.searchFlights(criteria);

        options = options.stream()
                .filter(f -> f.getFlightId() != currentFlight.getFlightId())
                .filter(f -> f.getAvailableSeats() >= passengerCount)
                .toList();

        if (options.isEmpty()) {

            System.out.println("No alternative flights found for that date");

            return;
        }

        options.forEach(System.out::println);

        System.out.print("Enter Flight ID to switch to : ");

        int newFlightId = Integer.parseInt(sc.nextLine());

        Flight newFlight = options.stream()
                .filter(f -> f.getFlightId() == newFlightId)
                .findFirst()
                .orElse(null);

        if (newFlight == null) {

            System.out.println("Invalid selection");

            return;
        }

        double fareDifference = (newFlight.getFare() - currentFlight.getFare()) * passengerCount;

        double totalCharge = fareDifference + FLIGHT_CHANGE_FEE;

        System.out.printf("Fare Difference : Rs.%.2f  |  Modification Fee : Rs.%.2f  |  Payable : Rs.%.2f%n",
                fareDifference, FLIGHT_CHANGE_FEE, totalCharge);

        if (totalCharge > 0) {

            Booking tempForPayment = new Booking();

            tempForPayment.setBookingId(booking.getBookingId());
            tempForPayment.setPnr(booking.getPnr());
            tempForPayment.setTotalFare(totalCharge);

            boolean paid = new PaymentService().processPayment(tempForPayment);

            if (!paid) {

                System.out.println("Payment failed - flight change aborted");

                return;
            }

        } else {

            System.out.printf("Rs.%.2f will be refunded to your original payment method%n", -totalCharge);
        }

        // release old flight's seats
        for (BookingPassenger p : passengers) {

            if (p.getSeatNumber() != null) {

                Seat oldSeat = seatRepository.findBySeatNumber(currentFlight.getFlightId(), p.getSeatNumber());

                if (oldSeat != null) {
                    seatRepository.updateStatus(oldSeat.getSeatId(), SeatStatus.AVAILABLE);
                }
            }
        }

        flightRepository.updateAvailableSeats(currentFlight.getFlightId(), passengerCount);
        flightRepository.updateAvailableSeats(newFlight.getFlightId(), -passengerCount);

        double newTotalFare = newFlight.getFare() * passengerCount + booking.getSeatCharges();

        bookingRepository.updateFlightAndFare(
                booking.getBookingId(), newFlight.getFlightId(), newTotalFare, booking.getSeatCharges());

        String revisedETicket = ETicketGenerator.generate(booking.getPnr());

        bookingRepository.updateETicket(booking.getBookingId(), revisedETicket);

        System.out.println("Flight changed. Revised E-Ticket : " + revisedETicket +
                "\nSelect your seats on the new flight from the Seat menu (Flight ID " +
                newFlight.getFlightId() + ")");
    }

    // ---------- 6.2 Passenger Details Modification ----------

    @Override
    public void modifyPassengerDetails() {

        Booking booking = fetchModifiableBooking();

        if (booking == null) {
            return;
        }

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId());

        for (int i = 0; i < passengers.size(); i++) {
            System.out.println((i + 1) + ". " + passengers.get(i).getName());
        }

        System.out.print("Select Passenger : ");

        int index = Integer.parseInt(sc.nextLine()) - 1;

        if (index < 0 || index >= passengers.size()) {

            System.out.println("Invalid selection");

            return;
        }

        BookingPassenger passenger = passengers.get(index);

        boolean nameChanged = false;
        String revisedETicket = null;
        examples.manager.NotificationManager.getInstance().sendModificationConfirmation(
                new examples.repository.UserRepository().findById(booking.getUserId()),
                booking.getPnr(), revisedETicket);

        System.out.print("New Name (blank to keep '" + passenger.getName() + "') : ");

        String newName = sc.nextLine();

        if (!newName.isBlank() && !newName.equals(passenger.getName())) {

            System.out.println("Name correction requires supporting ID documentation to be verified at check-in");

            passenger.setName(newName);

            nameChanged = true;
        }

        System.out.print("New Contact Email (blank to keep) : ");

        String email = sc.nextLine();

        if (!email.isBlank()) {
            passenger.setContactEmail(email);
        }

        System.out.print("New Contact Phone (blank to keep) : ");

        String phone = sc.nextLine();

        if (!phone.isBlank()) {
            passenger.setContactPhone(phone);
        }

        System.out.print("New Meal Preference (blank to keep) : ");

        String meal = sc.nextLine();

        if (!meal.isBlank()) {
            passenger.setMealPreference(MealPreference.valueOf(meal.toUpperCase()));
        }

        System.out.print("New Special Assistance (blank to keep) : ");

        String assistance = sc.nextLine();

        if (!assistance.isBlank()) {
            passenger.setSpecialAssistance(assistance);
        }

        if (nameChanged) {

            Booking tempForPayment = new Booking();

            tempForPayment.setBookingId(booking.getBookingId());
            tempForPayment.setPnr(booking.getPnr());
            tempForPayment.setTotalFare(NAME_CORRECTION_FEE);

            boolean paid = new PaymentService().processPayment(tempForPayment);

            if (!paid) {

                System.out.println("Payment failed - name correction not applied");

                return;
            }
        }

        passengerRepository.updateDetails(passenger);

        if (nameChanged) {

            revisedETicket = ETicketGenerator.generate(booking.getPnr());

            bookingRepository.updateETicket(booking.getBookingId(), revisedETicket);

            System.out.println("Details updated. Revised E-Ticket (name changed) : " + revisedETicket);

        } else {

            System.out.println("Details updated successfully");
        }
    }

    // ---------- 6.3 Seat Change ----------

    @Override
    public void changeSeat() {

        Booking booking = fetchModifiableBooking();

        if (booking == null) {
            return;
        }

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId());

        for (int i = 0; i < passengers.size(); i++) {

            System.out.println((i + 1) + ". " + passengers.get(i).getName() +
                    " - Current Seat : " + passengers.get(i).getSeatNumber());
        }

        System.out.print("Select Passenger : ");

        int index = Integer.parseInt(sc.nextLine()) - 1;

        if (index < 0 || index >= passengers.size()) {

            System.out.println("Invalid selection");

            return;
        }

        BookingPassenger passenger = passengers.get(index);

        new SeatService().displaySeatMap(booking.getFlightId());

        System.out.print("Enter New Seat Number : ");

        String newSeatNumber = sc.nextLine();

        Seat newSeat = seatRepository.findBySeatNumber(booking.getFlightId(), newSeatNumber);

        if (newSeat == null || newSeat.getStatus() != SeatStatus.AVAILABLE) {

            System.out.println("Seat unavailable");

            return;
        }

        Seat oldSeat = seatRepository.findBySeatNumber(booking.getFlightId(), passenger.getSeatNumber());

        double oldCharge = oldSeat == null ? 0 : oldSeat.getExtraCharge();

        double seatChangeCharge = newSeat.getExtraCharge() - oldCharge;

        if (seatChangeCharge > 0) {

            System.out.printf("Seat upgrade charge : Rs.%.2f%n", seatChangeCharge);

            Booking tempForPayment = new Booking();

            tempForPayment.setBookingId(booking.getBookingId());
            tempForPayment.setPnr(booking.getPnr());
            tempForPayment.setTotalFare(seatChangeCharge);

            boolean paid = new PaymentService().processPayment(tempForPayment);

            if (!paid) {

                System.out.println("Payment failed - seat not changed");

                return;
            }
        }

        if (oldSeat != null) {
            seatRepository.updateStatus(oldSeat.getSeatId(), SeatStatus.AVAILABLE);
        }

        seatRepository.updateStatus(newSeat.getSeatId(), SeatStatus.BOOKED);

        passengerRepository.updateSeatNumber(passenger.getPassengerBookingId(), newSeatNumber);

        double updatedSeatCharges = booking.getSeatCharges() + Math.max(seatChangeCharge, 0);

        bookingRepository.updateFlightAndFare(
                booking.getBookingId(), booking.getFlightId(),
                booking.getTotalFare() + Math.max(seatChangeCharge, 0), updatedSeatCharges);

        System.out.println("Seat changed from " + passenger.getSeatNumber() +
                " to " + newSeatNumber + " for " + passenger.getName());
    }

    // ---------- shared ----------

    private Booking fetchModifiableBooking() {

        System.out.print("Enter PNR : ");

        Booking booking = bookingRepository.findByPNR(sc.nextLine());

        if (booking == null) {

            System.out.println("Booking not found");

            return null;
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            System.out.println("Only CONFIRMED bookings can be modified (current status : " +
                    booking.getStatus() + ")");

            return null;
        }

        return booking;
    }
}