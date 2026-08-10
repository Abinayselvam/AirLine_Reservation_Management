package examples.util;

import examples.model.Airport;
import examples.repository.AirportRepository;
import examples.repository.irepository.IAirportRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory index over airport data so autocomplete/lookup doesn't hit the
 * DB on every keystroke-equivalent. Rebuilt on demand via refresh() -
 * call it after any airport add/update/deactivate (UC 9).
 */
public final class AirportIndex {

    private static final IAirportRepository repository = new AirportRepository();

    private static volatile Map<String, Airport> byCode = new ConcurrentHashMap<>();

    private static volatile List<Airport> allAirports = List.of();

    private AirportIndex() {}

    public static synchronized void refresh() {

        List<Airport> airports = repository.findAll();

        Map<String, Airport> codeMap = new ConcurrentHashMap<>();

        for (Airport a : airports) {
            codeMap.put(a.getCode().toUpperCase(), a);
        }

        byCode = codeMap;

        allAirports = airports;
    }

    public static Airport getByCode(String code) {

        if (allAirports.isEmpty()) refresh();

        return byCode.get(code.toUpperCase());
    }

    public static List<Airport> autocomplete(String prefix) {

        if (allAirports.isEmpty()) refresh();

        String p = prefix.toUpperCase();

        return allAirports.stream()
                .filter(a -> a.isActive() && (
                        a.getCode().toUpperCase().startsWith(p) ||
                                a.getName().toUpperCase().startsWith(p) ||
                                a.getCity().toUpperCase().startsWith(p)))
                .limit(10)
                .toList();
    }

    /**
     * "Nearby" here means other active airports in the same country -
     * there's no lat/long on Airport yet, so true geographic distance
     * isn't available. Flagged as a simplification, not a guess.
     */
    public static List<Airport> nearbyAirports(String code) {

        Airport origin = getByCode(code);

        if (origin == null) return List.of();

        return allAirports.stream()
                .filter(a -> a.isActive())
                .filter(a -> a.getCountry().equalsIgnoreCase(origin.getCountry()))
                .filter(a -> !a.getCode().equalsIgnoreCase(code))
                .limit(3)
                .toList();
    }
}