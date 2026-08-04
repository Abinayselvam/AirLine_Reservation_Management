package examples.service;

import examples.enums.FlightClass;
import examples.enums.FlightStatus;
import examples.enums.Permission;
import examples.model.Flight;
import examples.repository.FlightRepository;
import examples.repository.SeatRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.ISeatRepository;
import examples.service.iservice.IFlightManagementService;
import examples.util.AccessValidator;
import examples.util.SeatMapGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FlightManagementService implements IFlightManagementService {

    private final Scanner sc = new Scanner(System.in);

    private final IFlightRepository flightRepository = new FlightRepository();

    private final ISeatRepository seatRepository = new SeatRepository();

    @Override
    public void createFlight() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        Flight flight = new Flight();

        System.out.print("Airline Name : ");
        flight.setAirlineName(sc.nextLine());

        System.out.print("Flight Number : ");
        flight.setFlightNumber(sc.nextLine());

        System.out.print("Source Airport Code : ");
        flight.setSource(sc.nextLine());

        System.out.print("Destination Airport Code : ");
        flight.setDestination(sc.nextLine());

        System.out.print("Departure Date (yyyy-MM-dd) : ");
        flight.setDepartureDate(LocalDate.parse(sc.nextLine()));

        System.out.print("Departure Time (HH:mm) : ");
        flight.setDepartureTime(LocalTime.parse(sc.nextLine()));

        System.out.print("Arrival Time (HH:mm) : ");
        flight.setArrivalTime(LocalTime.parse(sc.nextLine()));

        System.out.print("Duration (minutes) : ");
        flight.setDuration(Integer.parseInt(sc.nextLine()));

        System.out.print("Base Fare : ");
        flight.setFare(Double.parseDouble(sc.nextLine()));

        System.out.print("Travel Class [ECONOMY/PREMIUM_ECONOMY/BUSINESS/FIRST_CLASS] : ");
        flight.setTravelClass(FlightClass.valueOf(sc.nextLine().toUpperCase()));

        System.out.print("Total Seat Capacity : ");
        int capacity = Integer.parseInt(sc.nextLine());

        flight.setTotalSeats(capacity);
        flight.setAvailableSeats(capacity);

        System.out.print("Number of Stops : ");
        flight.setStops(Integer.parseInt(sc.nextLine()));

        System.out.print("Aircraft Type : ");
        flight.setAircraftType(sc.nextLine());

        flight.setStatus(FlightStatus.ON_TIME);

        int flightId = flightRepository.save(flight);

        if (flightId == -1) {

            System.out.println("Failed to create flight");

            return;
        }

        seatRepository.saveAll(SeatMapGenerator.generate(flightId, capacity));

        System.out.println("Flight created successfully. Flight ID : " + flightId +
                " (seat map generated with " + capacity + " seats)");
    }

    @Override
    public void updateFlightSchedule() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        System.out.print("Flight ID : ");

        int flightId = Integer.parseInt(sc.nextLine());

        if (flightRepository.findById(flightId) == null) {

            System.out.println("Flight not found");

            return;
        }

        System.out.print("New Departure Date (yyyy-MM-dd) : ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        System.out.print("New Departure Time (HH:mm) : ");
        LocalTime depTime = LocalTime.parse(sc.nextLine());

        System.out.print("New Arrival Time (HH:mm) : ");
        LocalTime arrTime = LocalTime.parse(sc.nextLine());

        System.out.print("New Duration (minutes) : ");
        int duration = Integer.parseInt(sc.nextLine());

        boolean updated = flightRepository.updateSchedule(flightId, date, depTime, arrTime, duration);

        System.out.println(updated ? "Schedule updated" : "Update failed");
    }

    @Override
    public void updateFareStructure() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        System.out.print("Flight ID : ");

        int flightId = Integer.parseInt(sc.nextLine());

        System.out.print("New Fare : ");

        double fare = Double.parseDouble(sc.nextLine());

        boolean updated = flightRepository.updateFare(flightId, fare);

        System.out.println(updated ? "Fare updated" : "Update failed - flight not found");
    }

    @Override
    public void updateAircraftType() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        System.out.print("Flight ID : ");

        int flightId = Integer.parseInt(sc.nextLine());

        System.out.print("New Aircraft Type : ");

        String type = sc.nextLine();

        boolean updated = flightRepository.updateAircraftType(flightId, type);

        System.out.println(updated ? "Aircraft type updated" : "Update failed - flight not found");
    }

    @Override
    public void updateFlightStatus() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        System.out.print("Flight ID : ");

        int flightId = Integer.parseInt(sc.nextLine());

        System.out.print("New Status [ON_TIME/DELAYED/CANCELLED/BOARDING/DEPARTED] : ");

        FlightStatus status = FlightStatus.valueOf(sc.nextLine().toUpperCase());

        boolean updated = flightRepository.updateStatus(flightId, status);

        if (!updated) {

            System.out.println("Update failed - flight not found");

            return;
        }

        System.out.println("Status updated to " + status);

        if (status == FlightStatus.DELAYED || status == FlightStatus.CANCELLED) {

            // Notification dispatch belongs to UC 12; console print stands in for now
            System.out.println("[Notification] Passengers on Flight " + flightId +
                    " will be informed: flight is now " + status);
        }
    }

    @Override
    public void searchFlightsAdmin() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        System.out.print("Airline (blank to skip) : ");
        String airline = sc.nextLine();

        System.out.print("Source (blank to skip) : ");
        String source = sc.nextLine();

        System.out.print("Destination (blank to skip) : ");
        String destination = sc.nextLine();

        System.out.print("Status (blank to skip) : ");
        String status = sc.nextLine();

        System.out.print("From Date (yyyy-MM-dd, blank to skip) : ");
        String fromStr = sc.nextLine();

        System.out.print("To Date (yyyy-MM-dd, blank to skip) : ");
        String toStr = sc.nextLine();

        LocalDate from = fromStr.isBlank() ? null : LocalDate.parse(fromStr);
        LocalDate to = toStr.isBlank() ? null : LocalDate.parse(toStr);

        var stream = flightRepository.findAll().stream();

        if (!airline.isBlank())
            stream = stream.filter(f -> f.getAirlineName().equalsIgnoreCase(airline));

        if (!source.isBlank())
            stream = stream.filter(f -> f.getSource().equalsIgnoreCase(source));

        if (!destination.isBlank())
            stream = stream.filter(f -> f.getDestination().equalsIgnoreCase(destination));

        if (!status.isBlank())
            stream = stream.filter(f -> f.getStatus() == FlightStatus.valueOf(status.toUpperCase()));

        if (from != null)
            stream = stream.filter(f -> !f.getDepartureDate().isBefore(from));

        if (to != null)
            stream = stream.filter(f -> !f.getDepartureDate().isAfter(to));

        List<Flight> results = stream.collect(Collectors.toList());

        if (results.isEmpty()) {

            System.out.println("No matching flights");

            return;
        }

        results.forEach(System.out::println);
    }

    @Override
    public void viewOccupancyRates() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        flightRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Flight::occupancyPercentage).reversed())
                .forEach(f -> System.out.printf("[%d] %s %s : %.1f%% occupied (%d/%d seats)%n",
                        f.getFlightId(), f.getAirlineName(), f.getFlightNumber(),
                        f.occupancyPercentage(),
                        f.getTotalSeats() - f.getAvailableSeats(), f.getTotalSeats()));
    }

    @Override
    public void generateFlightReport() {

        if (!AccessValidator.validate(Permission.MANAGE_FLIGHTS)) return;

        List<Flight> flights = flightRepository.findAll();

        Map<String, Long> countByAirline = flights.stream()
                .collect(Collectors.groupingBy(Flight::getAirlineName, Collectors.counting()));

        Map<FlightStatus, Long> countByStatus = flights.stream()
                .collect(Collectors.groupingBy(Flight::getStatus, Collectors.counting()));

        double avgOccupancy = flights.stream()
                .mapToDouble(Flight::occupancyPercentage)
                .average()
                .orElse(0);

        System.out.println("\n===== FLIGHT REPORT =====");
        System.out.println("Total Flights : " + flights.size());

        System.out.printf("Average Occupancy : %.1f%%%n", avgOccupancy);

        System.out.println("\nFlights by Airline:");
        countByAirline.forEach((airline, count) -> System.out.println("  " + airline + " : " + count));

        System.out.println("\nFlights by Status:");
        countByStatus.forEach((status, count) -> System.out.println("  " + status + " : " + count));
    }
}