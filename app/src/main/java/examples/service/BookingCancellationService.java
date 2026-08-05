package examples.service;

import examples.enums.BookingStatus;
import examples.enums.SeatStatus;
import examples.model.*;
import examples.repository.*;
import examples.repository.irepository.IBookingPassengerRepository;
import examples.repository.irepository.IBookingRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.ISeatRepository;
import examples.service.iservice.IBookingCancellationService;
import examples.service.iservice.IPaymentService;
import examples.util.CancellationPolicyUtil;
import examples.util.ETicketGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class BookingCancellationService implements IBookingCancellationService {

    private final Scanner sc = new Scanner(System.in);

    private final IBookingRepository bookingRepository = new BookingRepository();

    private final IBookingPassengerRepository passengerRepository = new BookingPassengerRepository();

    private final IFlightRepository flightRepository = new FlightRepository();

    private final ISeatRepository seatRepository = new SeatRepository();

    private final IPaymentService paymentService = new PaymentService();

    @Override
    public void cancelFullBooking() {

        Booking booking = fetchCancellableBooking();

        if (booking == null) return;

        Flight flight = flightRepository.findById(booking.getFlightId());

        List<BookingPassenger> passengers = passengerRepository.findByBookingId(booking.getBookingId())
                .stream().filter(p -> !p.isCancelled()).toList();

        LocalDateTime departureDateTime =
                LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime());

        double chargePct = CancellationPolicyUtil.chargePercentage(departureDateTime);

        double deduction = booking.getTotalFare() * chargePct;

        double refundAmount = booking.getTotalFare() - deduction;

        System.out.println("Cancellation Policy : " +
                CancellationPolicyUtil.policyDescription(departureDateTime));

        System.out.printf("Total Paid : Rs.%.2f | Cancellation Charge : Rs.%.2f | Refund : Rs.%.2f%n",
                booking.getTotalFare(), deduction, refundAmount);

        System.out.print("Confirm Cancellation? (y/n) : ");

        if (!sc.nextLine().equalsIgnoreCase("y")) {

            System.out.println("Cancellation aborted");

            return;
        }

        booking.transitionTo(BookingStatus.CANCELLED);

        bookingRepository.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED);

        for (BookingPassenger p : passengers) {

            if (p.getSeatNumber() != null) {

                Seat seat = seatRepository.findBySeatNumber(flight.getFlightId(), p.getSeatNumber());

                if (seat != null) {
                    seatRepository.updateStatus(seat.getSeatId(), SeatStatus.AVAILABLE);
                }
            }

            passengerRepository.updateCancelled(p.getPassengerBookingId(), true);
        }

        flightRepository.updateAvailableSeats(flight.getFlightId(), passengers.size());

        if (refundAmount > 0) {

            var result = paymentService.processRefund(
                    booking.getUserId(), booking.getBookingId(), booking.getPnr(), refundAmount);
            examples.manager.NotificationManager.getInstance().sendCancellationConfirmation(
                    new examples.repository.UserRepository().findById(booking.getUserId()), booking.getPnr());

            System.out.println(result.getMessage());

        } else {

            System.out.println("No refund applicable per cancellation policy");
        }

        System.out.println("Booking " + booking.getPnr() + " CANCELLED successfully");
    }

    @Override
    public void cancelPartialBooking() {

        Booking booking = fetchCancellableBooking();

        if (booking == null) return;

        Flight flight = flightRepository.findById(booking.getFlightId());

        List<BookingPassenger> allPassengers = passengerRepository.findByBookingId(booking.getBookingId());

        List<BookingPassenger> active = allPassengers.stream()
                .filter(p -> !p.isCancelled()).toList();

        if (active.size() <= 1) {

            System.out.println("Only one active passenger remains - use full cancellation instead");

            return;
        }

        for (int i = 0; i < active.size(); i++) {
            System.out.println((i + 1) + ". " + active.get(i).getName() +
                    " - Seat " + active.get(i).getSeatNumber());
        }

        System.out.print("Enter passenger numbers to cancel (comma separated) : ");

        String[] selections = sc.nextLine().split(",");

        LocalDateTime departureDateTime =
                LocalDateTime.of(flight.getDepartureDate(), flight.getDepartureTime());

        double chargePct = CancellationPolicyUtil.chargePercentage(departureDateTime);

        double totalRefund = 0;

        double totalDeduction = 0;

        int cancelledCount = 0;

        for (String sel : selections) {

            int idx;

            try {
                idx = Integer.parseInt(sel.trim()) - 1;
            } catch (NumberFormatException e) {
                continue;
            }

            if (idx < 0 || idx >= active.size()) continue;

            BookingPassenger p = active.get(idx);

            double seatCharge = 0;

            if (p.getSeatNumber() != null) {

                Seat seat = seatRepository.findBySeatNumber(flight.getFlightId(), p.getSeatNumber());

                if (seat != null) {

                    seatCharge = seat.getExtraCharge();

                    seatRepository.updateStatus(seat.getSeatId(), SeatStatus.AVAILABLE);
                }
            }

            double passengerTotal = flight.getFare() + seatCharge;

            double deduction = passengerTotal * chargePct;

            double refund = passengerTotal - deduction;

            totalDeduction += deduction;

            totalRefund += refund;

            passengerRepository.updateCancelled(p.getPassengerBookingId(), true);

            cancelledCount++;

            System.out.printf("%s : Fare Rs.%.2f | Charge Rs.%.2f | Refund Rs.%.2f%n",
                    p.getName(), passengerTotal, deduction, refund);
        }

        if (cancelledCount == 0) {

            System.out.println("No valid passengers selected");

            return;
        }

        flightRepository.updateAvailableSeats(flight.getFlightId(), cancelledCount);

        double newTotalFare = booking.getTotalFare() - totalRefund - totalDeduction;

        bookingRepository.updateFlightAndFare(
                booking.getBookingId(), booking.getFlightId(), newTotalFare, booking.getSeatCharges());

        String revisedETicket = ETicketGenerator.generate(booking.getPnr());

        bookingRepository.updateETicket(booking.getBookingId(), revisedETicket);

        if (totalRefund > 0) {

            var result = paymentService.processRefund(booking.getUserId(),
                    booking.getBookingId(), booking.getPnr(), totalRefund);

            System.out.println(result.getMessage());
        }

        System.out.println(cancelledCount +
                " passenger(s) removed. Revised E-Ticket for remaining passengers : " + revisedETicket);

        System.out.println("Remaining passengers on this booking:");

        allPassengers.stream()
                .filter(p -> !p.isCancelled())
                .forEach(p -> System.out.println(" - " + p.getName() + " (Seat " + p.getSeatNumber() + ")"));
    }

    private Booking fetchCancellableBooking() {

        System.out.print("Enter PNR : ");

        Booking booking = bookingRepository.findByPNR(sc.nextLine());

        if (booking == null) {

            System.out.println("Booking not found");

            return null;
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            System.out.println("Only CONFIRMED bookings can be cancelled (current status : " +
                    booking.getStatus() + ")");

            return null;
        }

        return booking;
    }
}