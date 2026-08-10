package examples.exception;

public class BookingNotFoundException extends AirlineSystemException {

    public BookingNotFoundException(String reference) {
        super("BOOKING_NOT_FOUND", "No booking found for reference " + reference);
    }
}