package examples.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class BoardingPass {

    private String passId;
    private String pnr;
    private String passengerName;
    private String flightNumber;
    private String source;
    private String destination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime boardingTime;
    private String seatNumber;
    private String travelClass;

    public String getPassId() { return passId; }
    public void setPassId(String passId) { this.passId = passId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public LocalTime getBoardingTime() { return boardingTime; }
    public void setBoardingTime(LocalTime boardingTime) { this.boardingTime = boardingTime; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getTravelClass() { return travelClass; }
    public void setTravelClass(String travelClass) { this.travelClass = travelClass; }

    @Override
    public String toString() {

        return """

                ========== BOARDING PASS ==========
                Pass ID    : %s
                PNR        : %s
                Passenger  : %s
                Flight     : %s
                Route      : %s -> %s
                Date       : %s
                Departure  : %s   Boarding : %s
                Seat       : %s   Class    : %s
                ====================================
                """.formatted(passId, pnr, passengerName, flightNumber,
                source, destination, departureDate,
                departureTime, boardingTime, seatNumber, travelClass);
    }
}