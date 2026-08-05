package examples.service;

import examples.enums.BookingStatus;
import examples.enums.MealPreference;
import examples.enums.Permission;
import examples.enums.PaymentStatus;
import examples.model.Booking;
import examples.model.BookingPassenger;
import examples.model.Flight;
import examples.model.PaymentTransaction;
import examples.repository.*;
import examples.repository.irepository.IBookingPassengerRepository;
import examples.repository.irepository.IBookingRepository;
import examples.repository.irepository.IFlightRepository;
import examples.repository.irepository.IPaymentRepository;
import examples.service.iservice.IReportingService;
import examples.util.AccessValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReportingService implements IReportingService {

    private final Scanner sc = new Scanner(System.in);

    private final IBookingRepository bookingRepository = new BookingRepository();

    private final IBookingPassengerRepository passengerRepository = new BookingPassengerRepository();

    private final IFlightRepository flightRepository = new FlightRepository();

    private final IPaymentRepository paymentRepository = new PaymentRepository();

    private Map<Integer, Flight> flightMap() {

        return flightRepository.findAll().stream()
                .collect(Collectors.toMap(Flight::getFlightId, f -> f, (a, b) -> a));
    }

    @Override
    public void dailyBookingReport() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        System.out.print("Date (yyyy-MM-dd) : ");

        LocalDate date = LocalDate.parse(sc.nextLine());

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(b -> b.getCreatedAt().toLocalDate().equals(date))
                .toList();

        double revenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .mapToDouble(Booking::getTotalFare)
                .sum();

        long confirmed = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();

        long cancelled = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        System.out.println("\n===== DAILY BOOKING REPORT : " + date + " =====");
        System.out.println("Total Requests : " + bookings.size());
        System.out.println("Confirmed : " + confirmed + " | Cancelled : " + cancelled);
        System.out.printf("Revenue : Rs.%.2f%n", revenue);
    }

    @Override
    public void revenueReport() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        System.out.print("From Date (yyyy-MM-dd) : ");
        LocalDate from = LocalDate.parse(sc.nextLine());

        System.out.print("To Date (yyyy-MM-dd) : ");
        LocalDate to = LocalDate.parse(sc.nextLine());

        List<Booking> confirmed = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> {
                    LocalDate d = b.getCreatedAt().toLocalDate();
                    return !d.isBefore(from) && !d.isAfter(to);
                })
                .toList();

        double totalRevenue = confirmed.stream().mapToDouble(Booking::getTotalFare).sum();

        double avgBookingValue = confirmed.stream()
                .mapToDouble(Booking::getTotalFare)
                .average()
                .orElse(0);

        Map<LocalDate, Double> byDay = confirmed.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getCreatedAt().toLocalDate(),
                        Collectors.summingDouble(Booking::getTotalFare)));

        System.out.println("\n===== REVENUE REPORT : " + from + " to " + to + " =====");
        System.out.printf("Total Revenue : Rs.%.2f%n", totalRevenue);
        System.out.printf("Average Booking Value : Rs.%.2f%n", avgBookingValue);
        System.out.println("Bookings Counted : " + confirmed.size());

        System.out.println("\nBy Day:");
        byDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %s : Rs.%.2f%n", e.getKey(), e.getValue()));
    }

    @Override
    public void bookingTrendsByRoute() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        Map<Integer, Flight> flights = flightMap();

        Map<String, Long> byRoute = bookingRepository.findAll().stream()
                .filter(b -> flights.containsKey(b.getFlightId()))
                .collect(Collectors.groupingBy(
                        b -> {
                            Flight f = flights.get(b.getFlightId());
                            return f.getSource() + " -> " + f.getDestination();
                        },
                        Collectors.counting()));

        System.out.println("\n===== BOOKING TRENDS BY ROUTE =====");

        byRoute.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue() + " bookings"));
    }

    @Override
    public void bookingTrendsByAirline() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        Map<Integer, Flight> flights = flightMap();

        Map<String, Long> byAirline = bookingRepository.findAll().stream()
                .filter(b -> flights.containsKey(b.getFlightId()))
                .collect(Collectors.groupingBy(
                        b -> flights.get(b.getFlightId()).getAirlineName(),
                        Collectors.counting()));

        System.out.println("\n===== BOOKING TRENDS BY AIRLINE =====");

        byAirline.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue() + " bookings"));
    }

    @Override
    public void cancellationAndPaymentRates() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        List<Booking> all = bookingRepository.findAll();

        long total = all.size();

        long cancelled = all.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        double cancellationRate = total == 0 ? 0 : (cancelled * 100.0) / total;

        List<PaymentTransaction> payments = paymentRepository.findAll();

        long successCount = payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS).count();

        long failedCount = payments.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED).count();

        double successRate = payments.isEmpty() ? 0 : (successCount * 100.0) / payments.size();

        System.out.println("\n===== CANCELLATION / PAYMENT RATES =====");
        System.out.printf("Cancellation Rate : %.1f%% (%d of %d bookings)%n", cancellationRate, cancelled, total);
        System.out.printf("Payment Success Rate : %.1f%% (%d succeeded, %d failed, of %d attempts)%n",
                successRate, successCount, failedCount, payments.size());
    }

    @Override
    public void airlinePerformanceComparison() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        Map<Integer, Flight> flights = flightMap();

        Map<String, Double> revenueByAirline = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> flights.containsKey(b.getFlightId()))
                .collect(Collectors.groupingBy(
                        b -> flights.get(b.getFlightId()).getAirlineName(),
                        Collectors.summingDouble(Booking::getTotalFare)));

        Map<String, Double> avgOccupancyByAirline = flights.values().stream()
                .collect(Collectors.groupingBy(
                        Flight::getAirlineName,
                        Collectors.averagingDouble(Flight::occupancyPercentage)));

        System.out.println("\n===== AIRLINE PERFORMANCE =====");

        revenueByAirline.forEach((airline, revenue) ->
                System.out.printf("%s : Revenue Rs.%.2f | Avg Occupancy %.1f%%%n",
                        airline, revenue, avgOccupancyByAirline.getOrDefault(airline, 0.0)));
    }

    @Override
    public void peakBookingPeriods() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        Map<DayOfWeek, Long> byDayOfWeek = bookingRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        b -> b.getCreatedAt().getDayOfWeek(),
                        Collectors.counting()));

        Map<Integer, Long> byHour = bookingRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        b -> b.getCreatedAt().getHour(),
                        Collectors.counting()));

        System.out.println("\n===== PEAK BOOKING PERIODS =====");

        System.out.println("By Day of Week:");
        byDayOfWeek.entrySet().stream()
                .sorted(Map.Entry.<DayOfWeek, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue()));

        System.out.println("By Hour of Day:");
        byHour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println("  " + e.getKey() + ":00 : " + e.getValue()));
    }

    @Override
    public void seatUtilizationReport() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        flightRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Flight::occupancyPercentage).reversed())
                .forEach(f -> System.out.printf("[%d] %s %s : %.1f%% utilized (%d/%d seats)%n",
                        f.getFlightId(), f.getAirlineName(), f.getFlightNumber(),
                        f.occupancyPercentage(),
                        f.getTotalSeats() - f.getAvailableSeats(), f.getTotalSeats()));
    }

    @Override
    public void passengerDemographics() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        List<BookingPassenger> passengers = passengerRepository.findAll().stream()
                .filter(p -> !p.isCancelled())
                .toList();

        Map<String, Long> byAgeGroup = passengers.stream()
                .collect(Collectors.groupingBy(this::ageGroup, Collectors.counting()));

        Map<String, Long> byGender = passengers.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getGender() == null ? "Unspecified" : p.getGender(),
                        Collectors.counting()));

        System.out.println("\n===== PASSENGER DEMOGRAPHICS =====");

        System.out.println("By Age Group:");
        byAgeGroup.forEach((group, count) -> System.out.println("  " + group + " : " + count));

        System.out.println("By Gender:");
        byGender.forEach((gender, count) -> System.out.println("  " + gender + " : " + count));
    }

    private String ageGroup(BookingPassenger p) {

        int age = p.getAge();

        if (age < 2) return "Infant (0-1)";

        if (age < 12) return "Child (2-11)";

        if (age < 18) return "Teen (12-17)";

        if (age < 60) return "Adult (18-59)";

        return "Senior (60+)";
    }

    @Override
    public void repeatCustomersAndLTV() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        List<Booking> confirmed = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .toList();

        Map<Integer, Long> bookingCountByUser = confirmed.stream()
                .collect(Collectors.groupingBy(Booking::getUserId, Collectors.counting()));

        Map<Integer, Double> ltvByUser = confirmed.stream()
                .collect(Collectors.groupingBy(Booking::getUserId, Collectors.summingDouble(Booking::getTotalFare)));

        long repeatCustomers = bookingCountByUser.values().stream().filter(c -> c > 1).count();

        System.out.println("\n===== REPEAT CUSTOMERS & LIFETIME VALUE =====");
        System.out.println("Unique Customers : " + bookingCountByUser.size());
        System.out.println("Repeat Customers (2+ bookings) : " + repeatCustomers);

        System.out.println("\nTop 5 by Lifetime Value:");
        ltvByUser.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.printf("  User %d : Rs.%.2f (%d bookings)%n",
                        e.getKey(), e.getValue(), bookingCountByUser.get(e.getKey())));
    }

    @Override
    public void passengerPreferenceReport() {

        if (!AccessValidator.validate(Permission.VIEW_REPORTS)) return;

        List<BookingPassenger> passengers = passengerRepository.findAll().stream()
                .filter(p -> !p.isCancelled())
                .toList();

        Map<MealPreference, Long> byMeal = passengers.stream()
                .filter(p -> p.getMealPreference() != null)
                .collect(Collectors.groupingBy(BookingPassenger::getMealPreference, Collectors.counting()));

        long specialAssistanceCount = passengers.stream()
                .filter(p -> p.getSpecialAssistance() != null && !p.getSpecialAssistance().isBlank())
                .count();

        double avgPassengersPerBooking = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .mapToInt(b -> passengerRepository.findByBookingId(b.getBookingId()).size())
                .average()
                .orElse(0);

        System.out.println("\n===== PASSENGER PREFERENCE REPORT =====");

        System.out.println("Meal Preferences:");
        byMeal.forEach((meal, count) -> System.out.println("  " + meal + " : " + count));

        System.out.println("Passengers Needing Special Assistance : " + specialAssistanceCount);

        System.out.printf("Average Passengers per Booking : %.1f%n", avgPassengersPerBooking);
    }
}