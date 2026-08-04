package examples.service;

import examples.enums.SeatCategory;
import examples.enums.SeatStatus;
import examples.model.Flight;
import examples.model.Seat;
import examples.repository.FlightRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.ISeatRepository;
import examples.repository.SeatRepository;
import examples.service.iservice.ISeatService;
import examples.util.SeatMapGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SeatService implements ISeatService {

    private final Scanner sc = new Scanner(System.in);

    private final ISeatRepository seatRepository = new SeatRepository();

    private final IFlightRepository flightRepository = new FlightRepository();

    private List<Seat> getOrCreateSeatMap(int flightId) {

        List<Seat> seats = seatRepository.findByFlightId(flightId);

        if (!seats.isEmpty()) {
            return seats;
        }

        Flight flight = flightRepository.findById(flightId);

        if (flight == null) {
            return List.of();
        }

        List<Seat> generated =
                SeatMapGenerator.generate(flightId, flight.getAvailableSeats());

        seatRepository.saveAll(generated);

        return seatRepository.findByFlightId(flightId);
    }

    @Override
    public void displaySeatMap(int flightId) {

        List<Seat> seats = getOrCreateSeatMap(flightId);

        if (seats.isEmpty()) {

            System.out.println("Flight not found or has no seat map");

            return;
        }

        System.out.println("\nLegend : O=Available  X=Booked  ~=Locked  #=Blocked  *=Premium/Exit");

        int col = 0;

        for (Seat seat : seats) {

            char marker = seat.statusSymbol();

            String tag = (seat.getCategory() == SeatCategory.PREMIUM
                    || seat.getCategory() == SeatCategory.EMERGENCY_EXIT) ? "*" : "";

            System.out.printf("%-6s[%c]%-1s ", seat.getSeatNumber(), marker, tag);

            col++;

            if (col % 6 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }

    @Override
    public void selectSeats(int flightId, int numberOfPassengers) {

        List<Seat> seats = getOrCreateSeatMap(flightId);

        if (seats.isEmpty()) {

            System.out.println("Flight not found");

            return;
        }

        List<Seat> chosen = new ArrayList<>();

        double totalCharge = 0;

        for (int i = 1; i <= numberOfPassengers; i++) {

            System.out.print("Passenger " + i + " - Enter Seat Number (blank to auto-assign) : ");

            String input = sc.nextLine();

            if (input.isBlank()) {
                continue;
            }

            Seat seat = seatRepository.findBySeatNumber(flightId, input);

            if (seat == null) {

                System.out.println("Invalid seat number, skipping");

                continue;
            }

            if (seat.getStatus() != SeatStatus.AVAILABLE) {

                System.out.println("Seat " + input + " is not available");

                i--;

                continue;
            }

            if (seat.getCategory() == SeatCategory.EMERGENCY_EXIT) {

                System.out.print("Emergency exit seat requires an able-bodied adult passenger. Confirm (y/n) : ");

                if (!sc.nextLine().equalsIgnoreCase("y")) {

                    i--;

                    continue;
                }
            }

            seatRepository.updateStatus(seat.getSeatId(), SeatStatus.LOCKED);

            chosen.add(seat);

            totalCharge += seat.getExtraCharge();

            System.out.println("Seat " + seat.getSeatNumber() + " locked" +
                    (seat.getExtraCharge() > 0
                            ? " (+Rs." + seat.getExtraCharge() + ")"
                            : ""));
        }

        int remaining = numberOfPassengers - chosen.size();

        if (remaining > 0) {

            System.out.println(remaining + " passenger(s) left without a seat - auto-assigning");

            autoAssignSeats(flightId, remaining);
        }

        System.out.printf("Total seat charges : Rs.%.2f%n", totalCharge);
    }

    @Override
    public void autoAssignSeats(int flightId, int numberOfPassengers) {

        List<Seat> seats = getOrCreateSeatMap(flightId);

        List<Seat> available = seats.stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE
                        && s.getCategory() == SeatCategory.STANDARD)
                .limit(numberOfPassengers)
                .toList();

        for (Seat seat : available) {

            seatRepository.updateStatus(seat.getSeatId(), SeatStatus.LOCKED);

            System.out.println("Auto-assigned seat " + seat.getSeatNumber());
        }

        if (available.size() < numberOfPassengers) {
            System.out.println("Not enough standard seats available for all passengers");
        }
    }
}