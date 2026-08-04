package examples.model;

import examples.enums.SeatCategory;
import examples.enums.SeatStatus;
import examples.enums.SeatType;

public class Seat {

    private int seatId;

    private int flightId;

    private String seatNumber;

    private SeatType seatType;

    private SeatCategory category;

    private SeatStatus status;

    private double extraCharge;

    private boolean powerOutlet;

    private boolean extraLegroom;

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }

    public SeatCategory getCategory() { return category; }
    public void setCategory(SeatCategory category) { this.category = category; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public double getExtraCharge() { return extraCharge; }
    public void setExtraCharge(double extraCharge) { this.extraCharge = extraCharge; }

    public boolean isPowerOutlet() { return powerOutlet; }
    public void setPowerOutlet(boolean powerOutlet) { this.powerOutlet = powerOutlet; }

    public boolean isExtraLegroom() { return extraLegroom; }
    public void setExtraLegroom(boolean extraLegroom) { this.extraLegroom = extraLegroom; }

    public char statusSymbol() {
        return switch (status) {
            case AVAILABLE -> 'O';
            case BOOKED -> 'X';
            case LOCKED -> '~';
            case BLOCKED -> '#';
        };
    }
}