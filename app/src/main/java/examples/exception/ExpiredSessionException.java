package examples.exception;

public class ExpiredSessionException extends AirlineSystemException {

    public ExpiredSessionException() {
        super("SESSION_EXPIRED", "Your session has expired - please log in again");
    }
}