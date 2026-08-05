package examples.service;

import examples.enums.BookingStatus;
import examples.enums.NotificationType;
import examples.enums.SeatStatus;
import examples.model.*;
import examples.repository.*;
import examples.repository.irepository.*;
import examples.service.iservice.ICheckInService;
import examples.util.BoardingPassGenerator;
import examples.util.CheckInWindowUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class CheckInService implements ICheckInService {

    private final Scanner sc = new Scanner(System.in);

    private final IBookingRepository bookingRepository = new BookingRepository();

    private final IBookingPassengerRepository passengerRepository = new BookingPassengerRepository();

    private final IFlightRepository flightRepository = new FlightRepository();

    private final ISeatRepository seatRepository = new SeatRepository();

    private final IAirportRepository airportRepository = new AirportRepository();

    @Override
    public void checkIn() {

        System.out.print("Enter PNR : ");

        Booking booking = bookingRepository.findByPNR(sc.nextLine());

        if (booking == null) {

            System.out.println("Booking not found");

            return;
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            System.out.println("Only CONFIRMED bookings can check in (current status : " +
                    booking.getStatus() + ")");

            return;
        }

        if (booking.isCheckInStatus()) {

            System.out.println("This booking has already checked in");

            return;
        }

        Flight flight = flightRepository.findById(booking.getFlightId());

        LocalDateTime departureDateTime =
                LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime());

        if (!CheckInWindowUtil.isWithinWindow(departureDateTime)) {

            System.out.println(CheckInWindowUtil.windowMessage(departureDateTime));

            return;
        }

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId())
                .stream().filter(p -> !p.isCancelled()).toList();

        if (!validateDocuments(passengers, flight)) {
            return;
        }

        confirmOrChangeSeats(booking, flight, passengers);

        List<BoardingPass> passes = passengers.stream()
                .map(p -> BoardingPassGenerator.generate(booking, p, flight))
                .toList();

        passes.forEach(System.out::println);

        bookingRepository.updateCheckInStatus(booking.getBookingId(), true);

        UserRepository userRepository = new UserRepository();

        User user = userRepository.findById(booking.getUserId());

        examples.manager.NotificationManager.getInstance().notifyUser(
                user, NotificationType.BOARDING_PASS,
                "Boarding Pass - " + booking.getPnr(),
                "Check-in complete for " + booking.getPnr() + ". " +
                        passes.size() + " boarding pass(es) generated.");

        System.out.println("Check-in complete for booking " + booking.getPnr());
    }

    private boolean validateDocuments(List<BookingPassenger> passengers, Flight flight) {

        boolean international = isInternational(flight);

        for (BookingPassenger p : passengers) {

            if (p.getIdProof() == null || p.getIdProof().isBlank()) {

                System.out.println("Check-in blocked: " + p.getName() + " has no ID document on file");

                return false;
            }

            if (international && p.getIdProof().length() < 6) {

                System.out.println("Check-in blocked: " + p.getName() +
                        " needs a valid passport for this international flight");

                return false;
            }

            if (p.getSpecialAssistance() != null && !p.getSpecialAssistance().isBlank()) {

                System.out.println("Special assistance noted for " + p.getName() +
                        " : " + p.getSpecialAssistance() + " - ground staff will be informed");
            }
        }

        return true;
    }

    private boolean isInternational(Flight flight) {

        Airport source = airportRepository.findByCode(flight.getSource());

        Airport destination = airportRepository.findByCode(flight.getDestination());

        if (source == null || destination == null) {
            return false; // unknown airport data - can't confirm, don't block on an assumption
        }

        return !source.getCountry().equalsIgnoreCase(destination.getCountry());
    }

    private void confirmOrChangeSeats(Booking booking, Flight flight, List<BookingPassenger> passengers) {

        for (BookingPassenger p : passengers) {

            System.out.print(p.getName() + " - Current Seat " + p.getSeatNumber() +
                    ". Change seat? (y/n) : ");

            if (sc.nextLine().equalsIgnoreCase("y")) {

                new SeatService().displaySeatMap(flight.getFlightId());

                System.out.print("New Seat Number : ");

                String newSeatNumber = sc.nextLine();

                Seat newSeat = seatRepository.findBySeatNumber(flight.getFlightId(), newSeatNumber);

                if (newSeat == null || newSeat.getStatus() != SeatStatus.AVAILABLE) {

                    System.out.println("Seat unavailable - keeping current seat");

                    continue;
                }

                Seat oldSeat = seatRepository.findBySeatNumber(flight.getFlightId(), p.getSeatNumber());

                if (oldSeat != null) {
                    seatRepository.updateStatus(oldSeat.getSeatId(), SeatStatus.AVAILABLE);
                }

                seatRepository.updateStatus(newSeat.getSeatId(), SeatStatus.BOOKED);

                passengerRepository.updateSeatNumber(p.getPassengerBookingId(), newSeatNumber);

                p.setSeatNumber(newSeatNumber);

                System.out.println("Seat updated to " + newSeatNumber);
            }
        }
    }
}