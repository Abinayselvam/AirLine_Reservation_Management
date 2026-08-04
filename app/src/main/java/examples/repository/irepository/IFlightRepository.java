package examples.repository.irepository;

import examples.model.Flight;
import examples.model.SearchCriteria;

import java.util.List;

public interface IFlightRepository {

    List<Flight> findAll();

    List<Flight> searchFlights(SearchCriteria criteria);

    Flight findById(int flightId);

    boolean updateAvailableSeats(int flightId, int delta);
    int save(Flight flight);

    boolean updateSchedule(int flightId, java.time.LocalDate departureDate,
                           java.time.LocalTime departureTime,
                           java.time.LocalTime arrivalTime, int duration);

    boolean updateFare(int flightId, double fare);

    boolean updateAircraftType(int flightId, String aircraftType);

    boolean updateStatus(int flightId, examples.enums.FlightStatus status);
}