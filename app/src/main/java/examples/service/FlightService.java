package examples.service;

import examples.enums.SortBy;
import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.repository.FlightRepository;
import examples.repository.irepository.IFlightRepository;
import examples.service.iservice.IFlightService;
import examples.util.FlightSearchCache;
import examples.util.PaginationUtil;

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
        String cacheKey = FlightSearchCache.buildKey(
                criteria.getSource(), criteria.getDestination(),
                date, criteria.getTravelClass() == null ? "ANY" : criteria.getTravelClass().name());

        FlightSearchCache.recordRouteSearch(criteria.getSource(), criteria.getDestination());

        List<Flight> results = FlightSearchCache.get(cacheKey);

        if (results == null) {

            results = repository.searchFlights(criteria);

            FlightSearchCache.put(cacheKey, results);

        } else {

            System.out.println("(served from cache)");
        }

        results = filterAndSort(results, criteria);

        if (results.isEmpty()) {

            System.out.println("No flights found for your search");

            return;
        }

        List<Flight> finalResults = results;
        PaginationUtil.paginate(finalResults, this::printFlight);
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

    @Override
    public void viewPriceCalendar() {

        System.out.print("Source Airport Code : ");
        String source = sc.nextLine();

        System.out.print("Destination Airport Code : ");
        String destination = sc.nextLine();

        System.out.print("Center Date (yyyy-MM-dd) : ");
        LocalDate centerDate = LocalDate.parse(sc.nextLine());

        System.out.println("\n===== PRICE CALENDAR (±3 days) =====");

        for (int offset = -3; offset <= 3; offset++) {

            LocalDate date = centerDate.plusDays(offset);

            SearchCriteria criteria = new SearchCriteria();

            criteria.setSource(source);
            criteria.setDestination(destination);
            criteria.setDepartureDate(date);

            List<Flight> flights = repository.searchFlights(criteria);

            if (flights.isEmpty()) {

                System.out.println("  " + date + " : No flights");

                continue;
            }

            double minFare = flights.stream().mapToDouble(Flight::getFare).min().orElse(0);

            double avgFare = flights.stream().mapToDouble(Flight::getFare).average().orElse(0);

            String marker = date.equals(centerDate) ? " <- selected" : "";

            System.out.printf("  %s : Cheapest Rs.%.2f | Avg Rs.%.2f | %d flight(s)%s%n",
                    date, minFare, avgFare, flights.size(), marker);
        }
    }

    @Override
    public void flexibleDateSearch() {

        System.out.print("Source Airport Code : ");
        String source = sc.nextLine();

        System.out.print("Destination Airport Code : ");
        String destination = sc.nextLine();

        System.out.print("Preferred Date (yyyy-MM-dd) : ");
        LocalDate preferredDate = LocalDate.parse(sc.nextLine());

        List<Flight> allOptions = new java.util.ArrayList<>();

        for (int offset = -3; offset <= 3; offset++) {

            SearchCriteria criteria = new SearchCriteria();

            criteria.setSource(source);
            criteria.setDestination(destination);
            criteria.setDepartureDate(preferredDate.plusDays(offset));

            allOptions.addAll(repository.searchFlights(criteria));
        }

        allOptions.sort(Comparator.comparingDouble(Flight::getFare));

        System.out.println("\n===== FLEXIBLE DATE RESULTS (sorted by price) =====");

        PaginationUtil.paginate(allOptions, this::printFlight);
    }

    @Override
    public void airportAutocomplete() {

        System.out.print("Type city, name, or code (min 2 characters) : ");

        String prefix = sc.nextLine();

        if (prefix.length() < 2) {

            System.out.println("Type at least 2 characters");

            return;
        }

        List<examples.model.Airport> matches = examples.util.AirportIndex.autocomplete(prefix);

        if (matches.isEmpty()) {

            System.out.println("No matching airports");

            return;
        }

        matches.forEach(a -> System.out.println("  " + a.getCode() + " - " + a.getName() +
                " (" + a.getCity() + ", " + a.getCountry() + ")"));

        System.out.print("Also show nearby airports for one of these codes? (code or blank) : ");

        String code = sc.nextLine();

        if (!code.isBlank()) {

            List<examples.model.Airport> nearby = examples.util.AirportIndex.nearbyAirports(code);

            if (nearby.isEmpty()) {

                System.out.println("No other airports found in the same country");

            } else {

                System.out.println("Other airports in the same country:");

                nearby.forEach(a -> System.out.println("  " + a.getCode() + " - " + a.getName() +
                        " (" + a.getCity() + ")"));
            }
        }
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