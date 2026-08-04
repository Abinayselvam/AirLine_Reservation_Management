package examples.repository;

import examples.model.Flight;
import examples.model.SearchCriteria;

import java.util.List;

public interface IFlightRepository {

    List<Flight> findAll();

    List<Flight> searchFlights(SearchCriteria criteria);

    Flight findById(int flightId);

    boolean updateAvailableSeats(int flightId, int delta);
}