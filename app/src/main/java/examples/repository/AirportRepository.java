package examples.repository;

import examples.model.Airport;
import examples.repository.irepository.IAirportRepository;
import examples.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AirportRepository implements IAirportRepository {

    @Override
    public boolean save(Airport airport) {

        String sql = """
                INSERT INTO airports
                (code, name, city, country, timezone, terminals, facilities,
                 contact_phone, contact_email, active)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, airport.getCode());
            ps.setString(2, airport.getName());
            ps.setString(3, airport.getCity());
            ps.setString(4, airport.getCountry());
            ps.setString(5, airport.getTimezone());
            ps.setString(6, String.join(",", airport.getTerminals()));
            ps.setString(7, String.join(",", airport.getFacilities()));
            ps.setString(8, airport.getContactPhone());
            ps.setString(9, airport.getContactEmail());
            ps.setBoolean(10, true);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Airport airport) {

        String sql = """
                UPDATE airports
                SET name = ?, city = ?, country = ?, timezone = ?,
                    terminals = ?, facilities = ?, contact_phone = ?, contact_email = ?
                WHERE code = ?
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, airport.getName());
            ps.setString(2, airport.getCity());
            ps.setString(3, airport.getCountry());
            ps.setString(4, airport.getTimezone());
            ps.setString(5, String.join(",", airport.getTerminals()));
            ps.setString(6, String.join(",", airport.getFacilities()));
            ps.setString(7, airport.getContactPhone());
            ps.setString(8, airport.getContactEmail());
            ps.setString(9, airport.getCode());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean setActive(String code, boolean active) {

        String sql = "UPDATE airports SET active = ? WHERE code = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setBoolean(1, active);
            ps.setString(2, code);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Airport findByCode(String code) {

        String sql = "SELECT * FROM airports WHERE code = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, code);

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
    public List<Airport> findByCity(String city) {
        return findMany("SELECT * FROM airports WHERE city LIKE ? AND active = true", "%" + city + "%");
    }

    @Override
    public List<Airport> findByName(String name) {
        return findMany("SELECT * FROM airports WHERE name LIKE ? AND active = true", "%" + name + "%");
    }

    @Override
    public List<Airport> findByCountry(String country) {
        return findMany("SELECT * FROM airports WHERE country = ? AND active = true", country);
    }

    private List<Airport> findMany(String sql, String param) {

        List<Airport> airports = new ArrayList<>();

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, param);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                airports.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return airports;
    }

    @Override
    public List<Airport> findAll() {

        List<Airport> airports = new ArrayList<>();

        String sql = "SELECT * FROM airports";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                airports.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return airports;
    }

    private Airport mapRow(ResultSet rs) throws Exception {

        Airport airport = new Airport();

        airport.setAirportId(rs.getInt("airport_id"));
        airport.setCode(rs.getString("code"));
        airport.setName(rs.getString("name"));
        airport.setCity(rs.getString("city"));
        airport.setCountry(rs.getString("country"));
        airport.setTimezone(rs.getString("timezone"));

        String terminals = rs.getString("terminals");
        airport.setTerminals(terminals == null || terminals.isBlank()
                ? new ArrayList<>() : new ArrayList<>(Arrays.asList(terminals.split(","))));

        String facilities = rs.getString("facilities");
        airport.setFacilities(facilities == null || facilities.isBlank()
                ? new ArrayList<>() : new ArrayList<>(Arrays.asList(facilities.split(","))));

        airport.setContactPhone(rs.getString("contact_phone"));
        airport.setContactEmail(rs.getString("contact_email"));
        airport.setActive(rs.getBoolean("active"));

        return airport;
    }
}