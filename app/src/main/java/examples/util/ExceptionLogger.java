package examples.util;

import examples.exception.AirlineSystemException;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public final class ExceptionLogger {

    private static final String LOG_FILE = "airline_error_log.txt";

    private ExceptionLogger() {}

    public static void log(Exception e) {

        String errorCode = (e instanceof AirlineSystemException ase) ? ase.getErrorCode() : "UNEXPECTED";

        String line = String.format("[%s] %s | %s : %s",
                LocalDateTime.now(), errorCode, e.getClass().getSimpleName(), e.getMessage());

        System.err.println(line);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {

            writer.println(line);

        } catch (Exception fileError) {

            // Logging must never itself crash the app - fall back to console only.
            System.err.println("(also failed to write to log file: " + fileError.getMessage() + ")");
        }
    }

    /** Prints only the user-friendly part - never the stack trace - to the console the user sees. */
    public static void printFriendly(Exception e) {

        if (e instanceof AirlineSystemException) {

            System.out.println(e.getMessage());

        } else {

            System.out.println("Something went wrong. Please try again.");
        }

        log(e);
    }
}