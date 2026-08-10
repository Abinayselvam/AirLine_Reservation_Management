package examples.exception;

public class NetworkTimeoutException extends AirlineSystemException {

    public NetworkTimeoutException(String operation, Throwable cause) {
        super("NETWORK_TIMEOUT", "Timed out while " + operation, cause);
    }
}
