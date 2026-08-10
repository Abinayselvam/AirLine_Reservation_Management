package examples.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.repository.FlightRepository;
import examples.repository.irepository.IFlightRepository;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FlightSearchHandler implements HttpHandler {

    private final IFlightRepository repository = new FlightRepository();

    private final RateLimiter rateLimiter = new RateLimiter();

    @Override
    public void handle(HttpExchange exchange) throws java.io.IOException {

        String clientIp = exchange.getRemoteAddress().getAddress().getHostAddress();

        if (!rateLimiter.allow(clientIp)) {
            respond(exchange, 429, "{\"error\":\"Rate limit exceeded, try again in a minute\"}");
            return;
        }

        String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");

        if (!ApiKeyStore.isValid(apiKey)) {
            respond(exchange, 401, "{\"error\":\"Invalid or missing API key\"}");
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());

        SearchCriteria criteria = new SearchCriteria();

        criteria.setSource(params.get("source"));
        criteria.setDestination(params.get("destination"));

        if (params.containsKey("date")) {
            criteria.setDepartureDate(LocalDate.parse(params.get("date")));
        }

        List<Flight> results = repository.searchFlights(criteria);

        String json = results.stream()
                .map(this::toJson)
                .collect(Collectors.joining(",", "[", "]"));

        respond(exchange, 200, json);
    }

    private String toJson(Flight f) {

        return String.format(
                "{\"flightId\":%d,\"airline\":\"%s\",\"flightNumber\":\"%s\"," +
                        "\"source\":\"%s\",\"destination\":\"%s\",\"fare\":%.2f,\"availableSeats\":%d}",
                f.getFlightId(), f.getAirlineName(), f.getFlightNumber(),
                f.getSource(), f.getDestination(), f.getFare(), f.getAvailableSeats());
    }

    private Map<String, String> parseQuery(String query) {

        if (query == null || query.isBlank()) return Map.of();

        return java.util.Arrays.stream(query.split("&"))
                .map(pair -> pair.split("=", 2))
                .collect(Collectors.toMap(
                        kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        kv -> kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : ""));
    }

    private void respond(HttpExchange exchange, int status, String body) throws java.io.IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}