package examples.util;

import examples.enums.BookingStatus;

import java.util.Map;
import java.util.Set;

public final class BookingStateMachine {

    private static final Map<BookingStatus, Set<BookingStatus>> TRANSITIONS = Map.of(

            BookingStatus.INITIATED, Set.of(
                    BookingStatus.PASSENGER_DETAILS_ADDED, BookingStatus.CANCELLED),

            BookingStatus.PASSENGER_DETAILS_ADDED, Set.of(
                    BookingStatus.SEAT_SELECTED, BookingStatus.CANCELLED),

            BookingStatus.SEAT_SELECTED, Set.of(
                    BookingStatus.PAYMENT_PENDING, BookingStatus.CANCELLED),

            BookingStatus.PAYMENT_PENDING, Set.of(
                    BookingStatus.CONFIRMED, BookingStatus.CANCELLED),

            BookingStatus.CONFIRMED, Set.of(
                    BookingStatus.CANCELLED, BookingStatus.COMPLETED),

            BookingStatus.CANCELLED, Set.of(),

            BookingStatus.COMPLETED, Set.of()
    );

    private BookingStateMachine() {}

    public static boolean canTransition(BookingStatus from, BookingStatus to) {
        return TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }
}