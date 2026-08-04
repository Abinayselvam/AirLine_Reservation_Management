package examples.model;

import examples.enums.BookingPriority;

import java.time.LocalDateTime;

public class BookingRequest {

    private int requestId;
    private int userId;
    private int flightId;
    private int passengerCount;
    private BookingPriority priority;
    private LocalDateTime requestTime;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }

    public BookingPriority getPriority() { return priority; }
    public void setPriority(BookingPriority priority) { this.priority = priority; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
}