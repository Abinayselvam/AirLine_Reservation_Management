package examples.service.iservice;

public interface IAirportService {

    void addAirport();

    void updateAirport();

    void toggleActive();

    void searchByCode();

    void searchByCity();

    void searchByName();

    void listByCountry();
}