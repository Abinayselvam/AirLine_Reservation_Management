package examples.repository;

import examples.enums.MealPreference;
import examples.model.BookingPassenger;
import examples.repository.irepository.IBookingPassengerRepository;
import examples.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingPassengerRepository implements IBookingPassengerRepository {

    @Override
    public boolean saveAll(List<BookingPassenger> passengers) {

        String sql = """
                INSERT INTO booking_passengers
                (booking_id, name, age, gender, id_proof, meal_preference,
                 special_assistance, frequent_flyer_number, seat_number,
                 contact_email, contact_phone)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            for (BookingPassenger p : passengers) {

                ps.setInt(1, p.getBookingId());
                ps.setString(2, p.getName());
                ps.setInt(3, p.getAge());
                ps.setString(4, p.getGender());
                ps.setString(5, p.getIdProof());
                ps.setString(6, p.getMealPreference() == null ? null : p.getMealPreference().name());
                ps.setString(7, p.getSpecialAssistance());
                ps.setString(8, p.getFrequentFlyerNumber());
                ps.setString(9, p.getSeatNumber());
                ps.setString(10, p.getContactEmail());
                ps.setString(11, p.getContactPhone());

                ps.addBatch();
            }

            return ps.executeBatch().length == passengers.size();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<BookingPassenger> findByBookingId(int bookingId) {

        List<BookingPassenger> passengers = new ArrayList<>();

        String sql = "SELECT * FROM booking_passengers WHERE booking_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, bookingId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                BookingPassenger p = new BookingPassenger();

                p.setPassengerBookingId(rs.getInt("passenger_booking_id"));
                p.setBookingId(rs.getInt("booking_id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setGender(rs.getString("gender"));
                p.setIdProof(rs.getString("id_proof"));

                String meal = rs.getString("meal_preference");
                p.setMealPreference(meal == null ? null : MealPreference.valueOf(meal));

                p.setSpecialAssistance(rs.getString("special_assistance"));
                p.setFrequentFlyerNumber(rs.getString("frequent_flyer_number"));
                p.setSeatNumber(rs.getString("seat_number"));
                p.setContactEmail(rs.getString("contact_email"));
                p.setContactPhone(rs.getString("contact_phone"));

                passengers.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return passengers;
    }
}