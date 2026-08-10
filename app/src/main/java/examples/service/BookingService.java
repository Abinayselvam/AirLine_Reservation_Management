package examples.service;

import examples.enums.BookingStatus;
import examples.enums.MealPreference;
import examples.enums.SeatStatus;
import examples.exception.AirlineSystemException;
import examples.exception.FlightNotFoundException;
import examples.exception.SeatUnavailableException;
import examples.manager.BookingManager;
import examples.model.*;
import examples.repository.*;
import examples.repository.irepository.IBookingPassengerRepository;
import examples.repository.irepository.IBookingRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.ISeatRepository;
import examples.service.iservice.IBookingService;
import examples.util.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BookingService implements IBookingService {

    private final Scanner sc = new Scanner(System.in);

    private final IFlightRepository flightRepository = new FlightRepository();

    private final ISeatRepository seatRepository = new SeatRepository();

    private final IBookingRepository bookingRepository = new BookingRepository();

    private final IBookingPassengerRepository passengerRepository = new BookingPassengerRepository();

    @Override
    public void createBooking() {

        User user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Login Required");

            return;
        }

        int flightId = InputValidator.readInt(sc, "Flight ID : ");

        Flight flight;

        try {

            flight = flightRepository.findById(flightId);

            if (flight == null) {
                throw new FlightNotFoundException(flightId);
            }

            if (flight.getAvailableSeats() <= 0) {
                throw new SeatUnavailableException("any seat on flight " + flightId);
            }

        } catch (AirlineSystemException e) {

            ExceptionLogger.printFriendly(e);

            return;
        }

        Booking booking = new Booking();

        booking.setPnr(PNRGenerator.generate());
        booking.setFlightId(flightId);
        booking.setUserId(user.getId());
        booking.setStatus(BookingStatus.INITIATED);
        booking.setCreatedAt(LocalDateTime.now());

        System.out.print("Number of Passengers (max 6) : ");

        int count = Integer.parseInt(sc.nextLine());

        String countError = BusinessRuleValidator.validatePassengerCount(count);

        if (countError != null) {

            System.out.println(countError);

            return;
        }

        List<BookingPassenger> passengers = collectPassengers(count);

        String infantError = BusinessRuleValidator.validateInfantsHaveAdult(passengers);

        if (infantError != null) {

            System.out.println(infantError + " - booking cancelled");

            return;
        }

        boolean international = isInternational(flight);

        for (BookingPassenger p : passengers) {

            String docError = BusinessRuleValidator.validateInternationalDocument(
                    p, flight.getDepartureDate(), international);

            if (docError != null) {

                System.out.println(docError + " - booking cancelled");

                return;
            }
        }

        booking.transitionTo(BookingStatus.PASSENGER_DETAILS_ADDED);

        System.out.println("\n-- Seat Selection --");

        new SeatService().selectSeats(flightId, count);

        assignLockedSeatsToPassengers(flightId, passengers);

        double seatCharges = seatRepository.findByFlightId(flightId).stream()
                .filter(s -> s.getStatus() == SeatStatus.LOCKED)
                .mapToDouble(Seat::getExtraCharge)
                .sum();

        booking.transitionTo(BookingStatus.SEAT_SELECTED);

        FareBreakdown fareBreakdown = FareCalculator.calculate(flight, count, seatCharges, 0, international);

        booking.setSeatCharges(seatCharges);
        booking.setTotalFare(fareBreakdown.getTotal());

        System.out.println(fareBreakdown);
        booking.setExpiryTime(LocalDateTime.now().plusMinutes(20));

        booking.transitionTo(BookingStatus.PAYMENT_PENDING);

        if (!examples.manager.BookingManager.getInstance().tryLockBookingAttempt(user.getId(), flightId)) {

            System.out.println("A booking for this flight is already in progress for your account");

            return;
        }

        try {
            boolean bookingSaved = BookingManager.getInstance().saveBooking(booking);

            if (!bookingSaved) {

                System.out.println("Could not save the booking - please try again");

                return;
            }

            passengers.forEach(p -> p.setBookingId(booking.getBookingId()));

            passengerRepository.saveAll(passengers);

            System.out.printf("%nPNR : %s  |  Total Payable : Rs.%.2f  |  Pay within 20 minutes%n",
                    booking.getPnr(), booking.getTotalFare());

            // --- Payment integration point (UC 5) ---
            boolean paid = new PaymentService().processPayment(booking);

            if (paid) {

                booking.transitionTo(BookingStatus.CONFIRMED);

                String eTicket = ETicketGenerator.generate(booking.getPnr());

                booking.setETicketNumber(eTicket);

                bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CONFIRMED);
                bookingRepository.updateETicket(booking.getBookingId(), eTicket);

                markSeats(flightId, SeatStatus.BOOKED);

                flightRepository.updateAvailableSeats(flightId, -count);

                System.out.println("Booking CONFIRMED. E-Ticket : " + eTicket);
                examples.manager.NotificationManager.getInstance()
                        .sendBookingConfirmation(user, booking.getPnr(), eTicket);

            } else {

                booking.transitionTo(BookingStatus.CANCELLED);

                bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);

                markSeats(flightId, SeatStatus.AVAILABLE);

                System.out.println("Booking CANCELLED - payment unsuccessful, seats released");
            }
        }  catch (AirlineSystemException e) {

        ExceptionLogger.printFriendly(e);

    } catch (Exception e) {

        System.out.println("Booking could not be completed due to an unexpected error.");

        ExceptionLogger.log(e);
    }
        finally {
            examples.manager.BookingManager.getInstance().releaseBookingAttempt(user.getId(), flightId);
        }

    }
    private boolean isInternational(Flight flight) {

        var airportRepository = new examples.repository.AirportRepository();

        var source = airportRepository.findByCode(flight.getSource());

        var destination = airportRepository.findByCode(flight.getDestination());

        if (source == null || destination == null) return false;

        return !source.getCountry().equalsIgnoreCase(destination.getCountry());
    }

    private List<BookingPassenger> collectPassengers(int count) {

        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> {

                    System.out.println("\nPassenger " + i);

                    BookingPassenger p = new BookingPassenger();

                    System.out.print("Name : ");
                    p.setName(sc.nextLine());

                    System.out.print("Age : ");
                    p.setAge(Integer.parseInt(sc.nextLine()));

                    System.out.print("Gender : ");
                    p.setGender(sc.nextLine());

                    System.out.print("ID Proof : ");
                    p.setIdProof(sc.nextLine());

                    System.out.print("Meal Preference (VEG/NON_VEG/JAIN/VEGAN) : ");
                    String meal = sc.nextLine();
                    if (!meal.isBlank()) p.setMealPreference(MealPreference.valueOf(meal.toUpperCase()));

                    System.out.print("Special Assistance (blank if none) : ");
                    p.setSpecialAssistance(sc.nextLine());

                    System.out.print("Frequent Flyer No. (blank if none) : ");
                    p.setFrequentFlyerNumber(sc.nextLine());

                    System.out.print("Contact Email : ");
                    p.setContactEmail(sc.nextLine());

                    System.out.print("Contact Phone : ");
                    p.setContactPhone(sc.nextLine());
                    System.out.print("ID Proof Expiry Date (yyyy-MM-dd, blank if unknown) : ");
                    String expiry = sc.nextLine();
                    if (!expiry.isBlank()) p.setIdProofExpiryDate(java.time.LocalDate.parse(expiry));

                    return p;

                }).collect(Collectors.toList());

    }

    private void assignLockedSeatsToPassengers(int flightId, List<BookingPassenger> passengers) {

        List<Seat> locked = seatRepository.findByFlightId(flightId).stream()
                .filter(s -> s.getStatus() == SeatStatus.LOCKED)
                .toList();

        for (int i = 0; i < passengers.size() && i < locked.size(); i++) {
            passengers.get(i).setSeatNumber(locked.get(i).getSeatNumber());
        }
    }

    private void markSeats(int flightId, SeatStatus status) {

        seatRepository.findByFlightId(flightId).stream()
                .filter(s -> s.getStatus() == SeatStatus.LOCKED)
                .forEach(s -> seatRepository.updateStatus(s.getSeatId(), status));
    }

    @Override
    public void viewBookingByPNR() {

        System.out.print("Enter PNR : ");

        Booking booking = BookingManager.getInstance().getBookingByPNR(sc.nextLine());

        printBookingDetails(booking);
    }

    @Override
    public void viewBookingByContact() {

        System.out.print("Enter Email or Phone : ");

        List<Booking> bookings = bookingRepository.findByContact(sc.nextLine());

        if (bookings.isEmpty()) {

            System.out.println("No bookings found");

            return;
        }

        bookings.forEach(this::printBookingDetails);
    }

    @Override
    public void viewBookingByETicket() {

        System.out.print("Enter E-Ticket Number : ");

        Booking booking = bookingRepository.findByETicket(sc.nextLine());

        printBookingDetails(booking);
    }

    @Override
    public void viewMyBookings() {

        User user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Login Required");

            return;
        }

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        System.out.print("Filter [1-All 2-Upcoming(Confirmed) 3-Cancelled] : ");

        int filter = Integer.parseInt(sc.nextLine());

        var stream = bookings.stream();

        stream = switch (filter) {
            case 2 -> stream.filter(b -> b.getStatus() == BookingStatus.CONFIRMED);
            case 3 -> stream.filter(b -> b.getStatus() == BookingStatus.CANCELLED);
            default -> stream;
        };

        System.out.print("Sort [1-Newest First 2-Oldest First] : ");

        int sortChoice = Integer.parseInt(sc.nextLine());

        var comparator = java.util.Comparator.comparing(Booking::getCreatedAt);

        if (sortChoice == 1) comparator = comparator.reversed();

        stream.sorted(comparator).forEach(b ->
                System.out.printf("PNR:%s | Flight:%d | %s | Rs.%.2f | %s%n",
                        b.getPnr(), b.getFlightId(), b.getStatus(),
                        b.getTotalFare(), b.getCreatedAt()));
    }

    private void printBookingDetails(Booking booking) {

        if (booking == null) {

            System.out.println("Booking not found");

            return;
        }

        Flight flight = flightRepository.findById(booking.getFlightId());

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId());

        System.out.println("\n===== BOOKING DETAILS =====");
        System.out.println("PNR : " + booking.getPnr());
        System.out.println("E-Ticket : " + booking.getETicketNumber());
        System.out.println("Status : " + booking.getStatus());

        if (flight != null) {
            System.out.println("Flight : " + flight.getAirlineName() + " " +
                    flight.getFlightNumber() + " | " + flight.getSource() +
                    " -> " + flight.getDestination() + " on " + flight.getDepartureDate());
        }

        System.out.printf("Total Fare : Rs.%.2f (Seat Charges : Rs.%.2f)%n",
                booking.getTotalFare(), booking.getSeatCharges());

        System.out.println("Check-in : " + (booking.isCheckInStatus() ? "Done" : "Pending"));

        passengers.stream().filter(p -> !p.isCancelled()).forEach(p ->
                System.out.println(" - " + p.getName() + " (Age " + p.getAge() +
                        ") | Seat " + p.getSeatNumber() + " | Meal " + p.getMealPreference()));

    }
}