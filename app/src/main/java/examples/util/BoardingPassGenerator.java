package examples.util;

import examples.model.BoardingPass;
import examples.model.Booking;
import examples.model.BookingPassenger;
import examples.model.Flight;

public final class BoardingPassGenerator {

    private BoardingPassGenerator() {}

    public static BoardingPass generate(Booking booking, BookingPassenger passenger, Flight flight) {

        BoardingPass pass = new BoardingPass();

        pass.setPassId("BP" + System.currentTimeMillis() + passenger.getPassengerBookingId());
        pass.setPnr(booking.getPnr());
        pass.setPassengerName(passenger.getName());
        pass.setFlightNumber(flight.getAirlineName() + " " + flight.getFlightNumber());
        pass.setSource(flight.getSource());
        pass.setDestination(flight.getDestination());
        pass.setDepartureDate(flight.getDepartureDate());
        pass.setDepartureTime(flight.getDepartureTime());
        pass.setBoardingTime(flight.getDepartureTime().minusMinutes(45));
        pass.setSeatNumber(passenger.getSeatNumber() == null ? "Not Assigned" : passenger.getSeatNumber());
        pass.setTravelClass(flight.getTravelClass().name());

        return pass;
    }
}