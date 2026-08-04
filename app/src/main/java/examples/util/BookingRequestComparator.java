package examples.util;

import examples.enums.BookingPriority;
import examples.model.BookingRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class BookingRequestComparator implements Comparator<BookingRequest> {

    private static final long STARVATION_THRESHOLD_SECONDS = 120; // 2 minutes

    @Override
    public int compare(BookingRequest a, BookingRequest b) {

        int rankA = effectiveRank(a);

        int rankB = effectiveRank(b);

        if (rankA != rankB) {
            return Integer.compare(rankA, rankB); // lower rank = processed first
        }

        return a.getRequestTime().compareTo(b.getRequestTime());
    }

    private int effectiveRank(BookingRequest request) {

        if (request.getPriority() == BookingPriority.EXPRESS) {
            return 0;
        }

        long waitedSeconds =
                Duration.between(request.getRequestTime(), LocalDateTime.now()).getSeconds();

        // Starvation prevention: a Regular request waiting too long
        // is promoted to the same effective rank as Express.
        return waitedSeconds >= STARVATION_THRESHOLD_SECONDS ? 0 : 1;
    }
}