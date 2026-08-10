package examples.api;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class ApiServer {

    public static void start(int port) throws java.io.IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/flights/search", new FlightSearchHandler());
        server.createContext("/api/bookings/", new BookingLookupHandler());

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));

        server.start();

        System.out.println("REST API listening on http://localhost:" + port);
        System.out.println("  GET /api/flights/search?source=DEL&destination=BLR&date=2026-09-01");
        System.out.println("  GET /api/bookings/{pnr}");
        System.out.println("  (header X-API-Key: demo-key-123 required on all requests)");
    }
}