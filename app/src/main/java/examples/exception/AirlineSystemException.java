package examples.exception;

public abstract class AirlineSystemException extends RuntimeException {

    private final String errorCode;

    protected AirlineSystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected AirlineSystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}