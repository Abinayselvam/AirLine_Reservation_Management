package examples.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import examples.model.Booking;
import examples.repository.BookingRepository;
import examples.repository.irepository.IBookingRepository;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BookingLookupHandler implements HttpHandler {

    private final IBookingRepository repository = new BookingRepository();

    private final RateLimiter rateLimiter = new RateLimiter();

    @Override
    public void handle(HttpExchange exchange) throws java.io.IOException {

        String clientIp = exchange.getRemoteAddress().getAddress().getHostAddress();

        if (!rateLimiter.allow(clientIp)) {
            respond(exchange, 429, "{\"error\":\"Rate limit exceeded\"}");
            return;
        }

        if (!ApiKeyStore.isValid(exchange.getRequestHeaders().getFirst("X-API-Key"))) {
            respond(exchange, 401, "{\"error\":\"Invalid or missing API key\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath(); // /api/bookings/{pnr}

        String pnr = path.substring(path.lastIndexOf('/') + 1);

        Booking booking = repository.findByPNR(pnr);

        if (booking == null) {
            respond(exchange, 404, "{\"error\":\"Booking not found\"}");
            return;
        }

        String json = String.format(
                "{\"pnr\":\"%s\",\"status\":\"%s\",\"totalFare\":%.2f,\"eTicket\":\"%s\"}",
                booking.getPnr(), booking.getStatus(), booking.getTotalFare(),
                booking.getETicketNumber());

        respond(exchange, 200, json);
    }

    private void respond(HttpExchange exchange, int status, String body) throws java.io.IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}