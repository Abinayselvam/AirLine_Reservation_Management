package examples.service.iservice;

public interface IFlightService {

    void searchFlights();

    void viewGroupedByAirline();

    void viewAverageFareByAirline();

    void viewCheapestFlightsByRoute();

    void viewGroupedByPriceRange();

    void viewGroupedByDepartureSlot();
}