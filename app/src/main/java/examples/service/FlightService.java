package examples.service;

import examples.enums.SortBy;
import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.repository.FlightRepository;
import examples.repository.irepository.IFlightRepository;
import examples.service.iservice.IFlightService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FlightService implements IFlightService {

    private final Scanner sc = new Scanner(System.in);

    private final IFlightRepository repository = new FlightRepository();

    @Override
    public void searchFlights() {

        SearchCriteria criteria = new SearchCriteria();

        System.out.print("Source Airport Code : ");
        criteria.setSource(sc.nextLine());

        System.out.print("Destination Airport Code : ");
        criteria.setDestination(sc.nextLine());

        System.out.print("Departure Date (yyyy-MM-dd) : ");
        String date = sc.nextLine();

        if (!date.isBlank()) {
            criteria.setDepartureDate(LocalDate.parse(date));
        }

        System.out.print("Adults : ");
        criteria.setAdults(Integer.parseInt(sc.nextLine()));

        System.out.print("Children (0 if none) : ");
        criteria.setChildren(Integer.parseInt(sc.nextLine()));

        System.out.print("Infants (0 if none) : ");
        criteria.setInfants(Integer.parseInt(sc.nextLine()));

        System.out.print("Max Stops (-1 for any) : ");
        criteria.setStops(Integer.parseInt(sc.nextLine()));

        System.out.print("Preferred Airline (blank for any) : ");
        String airline = sc.nextLine();
        criteria.setAirline(airline.isBlank() ? null : airline);

        System.out.print("Sort By [1-Price Low-High 2-Price High-Low 3-Duration 4-Departure Time] : ");
        int sortChoice = Integer.parseInt(sc.nextLine());

        criteria.setSortBy(switch (sortChoice) {
            case 1 -> SortBy.PRICE_LOW_TO_HIGH;
            case 2 -> SortBy.PRICE_HIGH_TO_LOW;
            case 3 -> SortBy.DURATION_SHORTEST;
            case 4 -> SortBy.DEPARTURE_TIME;
            default -> null;
        });

        List<Flight> results = repository.searchFlights(criteria);

        results = filterAndSort(results, criteria);

        if (results.isEmpty()) {

            System.out.println("No flights found for your search");

            return;
        }

        System.out.println("\n===== SEARCH RESULTS =====");

        results.forEach(this::printFlight);
    }

    private List<Flight> filterAndSort(List<Flight> flights, SearchCriteria criteria) {

        var stream = flights.stream();

        if (criteria.getStops() >= 0) {
            stream = stream.filter(f -> f.getStops() <= criteria.getStops());
        }

        if (criteria.getAirline() != null) {
            stream = stream.filter(f ->
                    f.getAirlineName().equalsIgnoreCase(criteria.getAirline()));
        }

        if (criteria.getMaximumPrice() > 0) {
            stream = stream.filter(f -> f.getFare() <= criteria.getMaximumPrice());
        }

        if (criteria.getMinimumPrice() > 0) {
            stream = stream.filter(f -> f.getFare() >= criteria.getMinimumPrice());
        }

        Comparator<Flight> comparator = switch (criteria.getSortBy() == null
                ? SortBy.PRICE_LOW_TO_HIGH : criteria.getSortBy()) {

            case PRICE_LOW_TO_HIGH -> Comparator.comparingDouble(Flight::getFare);
            case PRICE_HIGH_TO_LOW -> Comparator.comparingDouble(Flight::getFare).reversed();
            case DURATION_SHORTEST -> Comparator.comparingInt(Flight::getDuration);
            case DEPARTURE_TIME -> Comparator.comparing(Flight::getDepartureTime);
        };

        return stream.sorted(comparator).collect(Collectors.toList());
    }

    private void printFlight(Flight f) {

        double taxes = f.getFare() * 0.05;

        double total = f.getFare() + taxes;

        System.out.println("-----------------------------------------");
        System.out.println(f.getAirlineName() + " " + f.getFlightNumber() +
                " (" + f.getAircraftType() + ")");
        System.out.println(f.getSource() + " -> " + f.getDestination() +
                " on " + f.getDepartureDate());
        System.out.println("Depart " + f.getDepartureTime() +
                " | Arrive " + f.getArrivalTime() +
                " | Duration " + f.getFormattedDuration() +
                " | Stops " + f.getStops());
        System.out.println("Class : " + f.getTravelClass() +
                " | Seats Available : " + f.getAvailableSeats());
        System.out.printf("Base Fare : Rs.%.2f | Taxes : Rs.%.2f | Total : Rs.%.2f%n",
                f.getFare(), taxes, total);
        System.out.println("Status : " + f.getStatus());
    }

    @Override
    public void viewGroupedByAirline() {

        Map<String, List<Flight>> grouped = repository.findAll().stream()
                .collect(Collectors.groupingBy(Flight::getAirlineName));

        grouped.forEach((airline, list) -> {

            System.out.println("\n" + airline + " (" + list.size() + " flights)");

            list.forEach(this::printFlight);
        });
    }

    @Override
    public void viewAverageFareByAirline() {

        Map<String, Double> avgFares = repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Flight::getAirlineName,
                        Collectors.averagingDouble(Flight::getFare)));

        System.out.println("\n===== AVERAGE FARE BY AIRLINE =====");

        avgFares.forEach((airline, avg) ->
                System.out.printf("%s : Rs.%.2f%n", airline, avg));
    }

    @Override
    public void viewCheapestFlightsByRoute() {

        Map<String, Flight> cheapestByRoute = repository.findAll().stream()
                .collect(Collectors.toMap(
                        f -> f.getSource() + " -> " + f.getDestination(),
                        f -> f,
                        (f1, f2) -> f1.getFare() <= f2.getFare() ? f1 : f2));

        System.out.println("\n===== CHEAPEST FLIGHT PER ROUTE =====");

        cheapestByRoute.forEach((route, flight) -> {

            System.out.println("\n" + route);

            printFlight(flight);
        });
    }

    @Override
    public void viewGroupedByPriceRange() {

        Map<String, List<Flight>> grouped = repository.findAll().stream()
                .collect(Collectors.groupingBy(this::priceRangeCategory));

        grouped.forEach((range, list) -> {

            System.out.println("\n" + range + " (" + list.size() + " flights)");

            list.forEach(this::printFlight);
        });
    }

    private String priceRangeCategory(Flight f) {

        if (f.getFare() < 5000) return "Budget (< Rs.5000)";

        if (f.getFare() < 15000) return "Standard (Rs.5000 - Rs.15000)";

        return "Premium (> Rs.15000)";
    }

    @Override
    public void viewGroupedByDepartureSlot() {

        Map<String, List<Flight>> grouped = repository.findAll().stream()
                .collect(Collectors.groupingBy(this::departureSlot));

        grouped.forEach((slot, list) -> {

            System.out.println("\n" + slot + " (" + list.size() + " flights)");

            list.forEach(this::printFlight);
        });
    }

    private String departureSlot(Flight f) {

        LocalTime time = f.getDepartureTime();

        int hour = time.getHour();

        if (hour >= 5 && hour < 12) return "Morning (5AM-12PM)";

        if (hour >= 12 && hour < 17) return "Afternoon (12PM-5PM)";

        if (hour >= 17 && hour < 21) return "Evening (5PM-9PM)";

        return "Night (9PM-5AM)";
    }
}