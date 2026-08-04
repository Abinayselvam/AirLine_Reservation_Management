package examples.model;

import examples.enums.FlightClass;
import examples.enums.FlightStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class Flight {

    private int flightId;

    private String airlineName;

    private String flightNumber;

    private String source;

    private String destination;

    private LocalDate departureDate;

    private LocalTime departureTime;

    private LocalTime arrivalTime;

    private double fare;

    private FlightClass travelClass;

    private int availableSeats;

    private int stops;

    private FlightStatus status;

    private String aircraftType;

    private int duration; // in minutes
    private int totalSeats;

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public double occupancyPercentage() {

        if (totalSeats == 0) return 0;

        return ((double) (totalSeats - availableSeats) / totalSeats) * 100;
    }

    public Flight() {}

    public Flight(int flightId, String airlineName, String flightNumber,
                  String source, String destination,
                  LocalDate departureDate, LocalTime departureTime,
                  LocalTime arrivalTime, double fare,
                  FlightClass travelClass, int availableSeats,
                  int stops, FlightStatus status,
                  String aircraftType, int duration) {

        this.flightId = flightId;
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.travelClass = travelClass;
        this.availableSeats = availableSeats;
        this.stops = stops;
        this.status = status;
        this.aircraftType = aircraftType;
        this.duration = duration;
    }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public FlightClass getTravelClass() { return travelClass; }
    public void setTravelClass(FlightClass travelClass) { this.travelClass = travelClass; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getStops() { return stops; }
    public void setStops(int stops) { this.stops = stops; }

    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }

    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getFormattedDuration() {
        return (duration / 60) + "h " + (duration % 60) + "m";
    }

    @Override
    public String toString() {

        return String.format(
                "[%s] %s %s | %s -> %s | %s %s-%s | %s | Seats:%d | Stops:%d | Rs.%.2f | %s",
                flightId, airlineName, flightNumber, source, destination,
                departureDate, departureTime, arrivalTime,
                getFormattedDuration(), availableSeats, stops, fare, status);
    }
}