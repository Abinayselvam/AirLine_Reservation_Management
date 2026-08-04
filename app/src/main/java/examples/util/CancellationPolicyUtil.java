package examples.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class CancellationPolicyUtil {

    private CancellationPolicyUtil() {}

    public static double chargePercentage(LocalDateTime departureDateTime) {

        long hoursUntilDeparture =
                Duration.between(LocalDateTime.now(), departureDateTime).toHours();

        if (hoursUntilDeparture < 0) {
            return 1.0; // flight already departed
        }

        if (hoursUntilDeparture >= 24) {
            return 0.0;
        }

        if (hoursUntilDeparture >= 12) {
            return 0.25;
        }

        if (hoursUntilDeparture >= 2) {
            return 0.50;
        }

        return 1.0;
    }

    public static String policyDescription(LocalDateTime departureDateTime) {

        double pct = chargePercentage(departureDateTime);

        if (pct == 0.0) {
            return "Full refund (24+ hours before departure)";
        }

        if (pct == 1.0) {
            return "Non-refundable (less than 2 hours before departure, or already departed)";
        }

        return (int) (pct * 100) + "% cancellation charge applies";
    }
}