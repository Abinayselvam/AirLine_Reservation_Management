package examples.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    private static class WindowCounter {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }

    public synchronized boolean allow(String clientKey) {

        WindowCounter counter = counters.computeIfAbsent(clientKey, k -> new WindowCounter());

        long now = System.currentTimeMillis();

        if (now - counter.windowStart > 60_000) {

            counter.windowStart = now;

            counter.count = 0;
        }

        counter.count++;

        return counter.count <= MAX_REQUESTS_PER_MINUTE;
    }
}