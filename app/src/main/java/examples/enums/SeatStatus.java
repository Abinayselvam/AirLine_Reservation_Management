package examples.enums;

public enum SeatStatus {
    AVAILABLE,
    LOCKED,   // temporarily held during an in-progress booking
    BOOKED,
    BLOCKED   // reserved by the airline, not for sale
}