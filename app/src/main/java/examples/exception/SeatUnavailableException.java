package examples.exception;

public class SeatUnavailableException extends AirlineSystemException {

    public SeatUnavailableException(String seatNumber) {
        super("SEAT_UNAVAILABLE", "Seat " + seatNumber + " is not available");
    }
}