package examples.model;

import examples.enums.FlightClass;
import examples.enums.SortBy;
import java.time.LocalDate;

public class SearchCriteria {

    private String source;

    private String destination;

    private LocalDate departureDate;

    private LocalDate returnDate;

    private int adults;

    private int children;

    private int infants;

    private FlightClass travelClass;

    private double minimumPrice;

    private double maximumPrice;

    private int stops = -1; // -1 = no stop filter

    private String airline; // null = no airline filter

    private SortBy sortBy;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }

    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }

    public int getInfants() { return infants; }
    public void setInfants(int infants) { this.infants = infants; }

    public int getTotalPassengers() { return adults + children + infants; }

    public FlightClass getTravelClass() { return travelClass; }
    public void setTravelClass(FlightClass travelClass) { this.travelClass = travelClass; }

    public double getMinimumPrice() { return minimumPrice; }
    public void setMinimumPrice(double minimumPrice) { this.minimumPrice = minimumPrice; }

    public double getMaximumPrice() { return maximumPrice; }
    public void setMaximumPrice(double maximumPrice) { this.maximumPrice = maximumPrice; }

    public int getStops() { return stops; }
    public void setStops(int stops) { this.stops = stops; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public SortBy getSortBy() { return sortBy; }
    public void setSortBy(SortBy sortBy) { this.sortBy = sortBy; }
}