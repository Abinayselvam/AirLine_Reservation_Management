package examples.util;

import examples.model.Flight;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class FlightSearchCache {

    private static final long TTL_MILLIS = 5 * 60 * 1000; // 5 minutes

    private record CacheEntry(List<Flight> flights, long cachedAt) {

        boolean isExpired() {
            return System.currentTimeMillis() - cachedAt > TTL_MILLIS;
        }
    }

    private static final Map<String, CacheEntry> searchCache = new ConcurrentHashMap<>();

    private static final Map<String, AtomicInteger> routeFrequency = new ConcurrentHashMap<>();

    private FlightSearchCache() {}

    public static String buildKey(String source, String destination, String date, String travelClass) {
        return (source + "|" + destination + "|" + date + "|" + travelClass).toUpperCase();
    }

    public static List<Flight> get(String key) {

        CacheEntry entry = searchCache.get(key);

        if (entry == null || entry.isExpired()) {

            searchCache.remove(key);

            return null;
        }

        return entry.flights();
    }

    public static void put(String key, List<Flight> flights) {
        searchCache.put(key, new CacheEntry(flights, System.currentTimeMillis()));
    }

    public static void recordRouteSearch(String source, String destination) {

        String routeKey = (source + " -> " + destination).toUpperCase();

        routeFrequency.computeIfAbsent(routeKey, k -> new AtomicInteger()).incrementAndGet();
    }

    public static Map<String, Integer> topRoutes(int limit) {

        return routeFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .limit(limit)
                .collect(java.util.LinkedHashMap::new,
                        (map, e) -> map.put(e.getKey(), e.getValue().get()),
                        java.util.LinkedHashMap::putAll);
    }

    public static void invalidateAll() {
        searchCache.clear();
    }

    public static int cacheSize() {
        return searchCache.size();
    }
}