package examples.service.iservice;

public interface IFlightManagementService {

    void createFlight();

    void updateFlightSchedule();

    void updateFareStructure();

    void updateAircraftType();

    void updateFlightStatus();

    void searchFlightsAdmin();

    void viewOccupancyRates();

    void generateFlightReport();
}