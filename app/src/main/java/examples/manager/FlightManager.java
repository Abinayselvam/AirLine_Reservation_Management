package examples.manager;

import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.repository.FlightRepository;
import examples.repository.irepository.IFlightRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlightManager {

    private static volatile FlightManager instance;

    private final IFlightRepository repository = new FlightRepository();

    private final Map<Integer, Flight> flightCache = new ConcurrentHashMap<>();

    private final Object seatLock = new Object();

    private FlightManager() {}

    public static FlightManager getInstance() {

        if (instance == null) {

            synchronized (FlightManager.class) {

                if (instance == null) {
                    instance = new FlightManager();
                }
            }
        }

        return instance;
    }

    public Flight getFlight(int flightId) {

        return flightCache.computeIfAbsent(flightId, repository::findById);
    }

    public List<Flight> searchFlights(SearchCriteria criteria) {
        return repository.searchFlights(criteria); // per-search results, not cached
    }

    public int createFlight(Flight flight) {

        int id = repository.save(flight);

        if (id != -1) {
            flightCache.put(id, flight);
        }

        return id;
    }

    /**
     * Synchronized so concurrent booking attempts on the same flight
     * can't both succeed past the available-seat check.
     */
    public boolean allocateSeats(int flightId, int count) {

        synchronized (seatLock) {

            Flight flight = getFlight(flightId);

            if (flight == null || flight.getAvailableSeats() < count) {
                return false;
            }

            boolean updated = repository.updateAvailableSeats(flightId, -count);

            if (updated) {
                invalidate(flightId);
            }

            return updated;
        }
    }

    public boolean releaseSeats(int flightId, int count) {

        synchronized (seatLock) {

            boolean updated = repository.updateAvailableSeats(flightId, count);

            if (updated) {
                invalidate(flightId);
            }

            return updated;
        }
    }

    public boolean updateFlightStatus(int flightId, examples.enums.FlightStatus status) {

        boolean updated = repository.updateStatus(flightId, status);

        if (updated) {
            invalidate(flightId);
        }

        return updated;
    }

    public void invalidate(int flightId) {
        flightCache.remove(flightId);
    }

    public int cacheSize() {
        return flightCache.size();
    }
}