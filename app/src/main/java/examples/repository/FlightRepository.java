package examples.repository;

import examples.enums.FlightClass;
import examples.enums.FlightStatus;
import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightRepository implements IFlightRepository {

    @Override
    public List<Flight> findAll() {

        List<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                flights.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flights;
    }

    @Override
    public List<Flight> searchFlights(SearchCriteria criteria) {

        List<Flight> flights = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM flights WHERE 1=1");

        if (criteria.getSource() != null && !criteria.getSource().isBlank()) {
            sql.append(" AND source = ?");
        }

        if (criteria.getDestination() != null && !criteria.getDestination().isBlank()) {
            sql.append(" AND destination = ?");
        }

        if (criteria.getDepartureDate() != null) {
            sql.append(" AND departure_date = ?");
        }

        if (criteria.getTravelClass() != null) {
            sql.append(" AND travel_class = ?");
        }

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {

            int index = 1;

            if (criteria.getSource() != null && !criteria.getSource().isBlank()) {
                ps.setString(index++, criteria.getSource());
            }

            if (criteria.getDestination() != null && !criteria.getDestination().isBlank()) {
                ps.setString(index++, criteria.getDestination());
            }

            if (criteria.getDepartureDate() != null) {
                ps.setObject(index++, criteria.getDepartureDate());
            }

            if (criteria.getTravelClass() != null) {
                ps.setString(index++, criteria.getTravelClass().name());
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                flights.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flights;
    }

    private Flight mapRow(ResultSet rs) throws Exception {

        Flight flight = new Flight();

        flight.setFlightId(rs.getInt("flight_id"));
        flight.setAirlineName(rs.getString("airline_name"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setSource(rs.getString("source"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartureDate(rs.getObject("departure_date", LocalDate.class));
        flight.setDepartureTime(rs.getObject("departure_time", java.time.LocalTime.class));
        flight.setArrivalTime(rs.getObject("arrival_time", java.time.LocalTime.class));
        flight.setFare(rs.getDouble("fare"));
        flight.setTravelClass(FlightClass.valueOf(rs.getString("travel_class")));
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setStops(rs.getInt("stops"));
        flight.setStatus(FlightStatus.valueOf(rs.getString("status")));
        flight.setAircraftType(rs.getString("aircraft_type"));
        flight.setDuration(rs.getInt("duration"));

        return flight;
    }
}