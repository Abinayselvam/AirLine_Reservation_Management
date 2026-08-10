package examples.exception;

public class DatabaseConnectionException extends AirlineSystemException {

    public DatabaseConnectionException(String operation, Throwable cause) {
        super("DB_CONNECTION_FAILED", "A database error occurred during: " + operation, cause);
    }
}