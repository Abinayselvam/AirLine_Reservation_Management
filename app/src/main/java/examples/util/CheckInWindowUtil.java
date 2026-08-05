package examples.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class CheckInWindowUtil {

    private CheckInWindowUtil() {}

    /** Per the doc: check-in window is open between 24 hours and 3 hours before departure. */
    public static boolean isWithinWindow(LocalDateTime departureDateTime) {

        long hoursUntilDeparture =
                Duration.between(LocalDateTime.now(), departureDateTime).toHours();

        return hoursUntilDeparture <= 24 && hoursUntilDeparture >= 3;
    }

    public static String windowMessage(LocalDateTime departureDateTime) {

        long hoursUntilDeparture =
                Duration.between(LocalDateTime.now(), departureDateTime).toHours();

        if (hoursUntilDeparture > 24) {
            return "Check-in opens 24 hours before departure (currently " +
                    (hoursUntilDeparture - 24) + " hours too early)";
        }

        if (hoursUntilDeparture < 3) {
            return "Online check-in has closed - please check in at the airport counter";
        }

        return "Check-in window is open";
    }
}