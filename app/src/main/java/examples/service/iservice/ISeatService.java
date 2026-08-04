package examples.service.iservice;

public interface ISeatService {

    void displaySeatMap(int flightId);

    void selectSeats(int flightId, int numberOfPassengers);

    void autoAssignSeats(int flightId, int numberOfPassengers);
}
