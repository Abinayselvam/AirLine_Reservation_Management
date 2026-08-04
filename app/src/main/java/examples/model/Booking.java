package examples.model;

import examples.enums.BookingStatus;
import examples.util.BookingStateMachine;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private String pnr;
    private String eTicketNumber;
    private int flightId;
    private int userId;
    private BookingStatus status;
    private double totalFare;
    private double seatCharges;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private boolean checkInStatus;

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getETicketNumber() { return eTicketNumber; }
    public void setETicketNumber(String eTicketNumber) { this.eTicketNumber = eTicketNumber; }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }

    public double getSeatCharges() { return seatCharges; }
    public void setSeatCharges(double seatCharges) { this.seatCharges = seatCharges; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public boolean isCheckInStatus() { return checkInStatus; }
    public void setCheckInStatus(boolean checkInStatus) { this.checkInStatus = checkInStatus; }

    public void transitionTo(BookingStatus target) {

        if (!BookingStateMachine.canTransition(this.status, target)) {

            throw new IllegalStateException(
                    "Cannot move booking from " + status + " to " + target);
        }

        this.status = target;
    }

    public boolean isExpired() {

        return status == BookingStatus.PAYMENT_PENDING
                && expiryTime != null
                && LocalDateTime.now().isAfter(expiryTime);
    }
}