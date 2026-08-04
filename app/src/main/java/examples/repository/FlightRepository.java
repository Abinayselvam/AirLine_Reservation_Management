package examples.repository;

import examples.enums.FlightClass;
import examples.enums.FlightStatus;
import examples.model.Flight;
import examples.model.SearchCriteria;
import examples.repository.irepository.IFlightRepository;
import examples.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    public Flight findById(int flightId) {

        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, flightId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateAvailableSeats(int flightId, int delta) {

        String sql = """
                UPDATE flights
                SET available_seats = available_seats + ?
                WHERE flight_id = ? AND available_seats + ? >= 0
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, delta);
            ps.setInt(2, flightId);
            ps.setInt(3, delta);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int save(Flight flight) {

        String sql = """
                INSERT INTO flights
                (airline_name, flight_number, source, destination,
                 departure_date, departure_time, arrival_time, fare,
                 travel_class, available_seats, total_seats, stops,
                 status, aircraft_type, duration)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, flight.getAirlineName());
            ps.setString(2, flight.getFlightNumber());
            ps.setString(3, flight.getSource());
            ps.setString(4, flight.getDestination());
            ps.setObject(5, flight.getDepartureDate());
            ps.setObject(6, flight.getDepartureTime());
            ps.setObject(7, flight.getArrivalTime());
            ps.setDouble(8, flight.getFare());
            ps.setString(9, flight.getTravelClass().name());
            ps.setInt(10, flight.getAvailableSeats());
            ps.setInt(11, flight.getTotalSeats());
            ps.setInt(12, flight.getStops());
            ps.setString(13, flight.getStatus().name());
            ps.setString(14, flight.getAircraftType());
            ps.setInt(15, flight.getDuration());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                ResultSet keys = ps.getGeneratedKeys();

                if (keys.next()) {

                    int id = keys.getInt(1);

                    flight.setFlightId(id);

                    return id;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public boolean updateSchedule(int flightId, java.time.LocalDate departureDate,
                                  java.time.LocalTime departureTime,
                                  java.time.LocalTime arrivalTime, int duration) {

        String sql = """
                UPDATE flights
                SET departure_date = ?, departure_time = ?, arrival_time = ?, duration = ?
                WHERE flight_id = ?
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setObject(1, departureDate);
            ps.setObject(2, departureTime);
            ps.setObject(3, arrivalTime);
            ps.setInt(4, duration);
            ps.setInt(5, flightId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateFare(int flightId, double fare) {

        String sql = "UPDATE flights SET fare = ? WHERE flight_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setDouble(1, fare);
            ps.setInt(2, flightId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateAircraftType(int flightId, String aircraftType) {

        String sql = "UPDATE flights SET aircraft_type = ? WHERE flight_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, aircraftType);
            ps.setInt(2, flightId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateStatus(int flightId, FlightStatus status) {

        String sql = "UPDATE flights SET status = ? WHERE flight_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());
            ps.setInt(2, flightId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
        flight.setTotalSeats(rs.getInt("total_seats"));
        return flight;
    }
}