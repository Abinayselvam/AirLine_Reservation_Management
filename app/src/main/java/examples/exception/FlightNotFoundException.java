package examples.exception;

public class FlightNotFoundException extends AirlineSystemException {

    public FlightNotFoundException(int flightId) {
        super("FLIGHT_NOT_FOUND", "No flight found with ID " + flightId);
    }
}