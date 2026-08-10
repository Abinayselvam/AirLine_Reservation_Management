package examples.exception;

public class InvalidInputException extends AirlineSystemException {

    public InvalidInputException(String field, String reason) {
        super("INVALID_INPUT", field + " is invalid: " + reason);
    }
}