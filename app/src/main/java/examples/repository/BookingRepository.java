package examples.repository;

import examples.enums.BookingStatus;
import examples.model.Booking;
import examples.repository.irepository.IBookingRepository;
import examples.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository implements IBookingRepository {

    @Override
    public boolean save(Booking booking) {

        String sql = """
                INSERT INTO bookings
                (pnr, e_ticket_number, flight_id, user_id, status,
                 total_fare, seat_charges, created_at, expiry_time, check_in_status)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, booking.getPnr());
            ps.setString(2, booking.getETicketNumber());
            ps.setInt(3, booking.getFlightId());
            ps.setInt(4, booking.getUserId());
            ps.setString(5, booking.getStatus().name());
            ps.setDouble(6, booking.getTotalFare());
            ps.setDouble(7, booking.getSeatCharges());
            ps.setObject(8, booking.getCreatedAt());
            ps.setObject(9, booking.getExpiryTime());
            ps.setBoolean(10, booking.isCheckInStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                ResultSet keys = ps.getGeneratedKeys();

                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                }

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Booking findByPNR(String pnr) {
        return findOne("SELECT * FROM bookings WHERE pnr = ?", pnr);
    }

    @Override
    public Booking findByETicket(String eTicket) {
        return findOne("SELECT * FROM bookings WHERE e_ticket_number = ?", eTicket);
    }

    private Booking findOne(String sql, String param) {

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, param);

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
    public List<Booking> findByUserId(int userId) {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    @Override
    public List<Booking> findByContact(String emailOrPhone) {

        List<Booking> bookings = new ArrayList<>();

        String sql = """
                SELECT DISTINCT b.* FROM bookings b
                JOIN booking_passengers bp ON b.booking_id = bp.booking_id
                WHERE bp.contact_email = ? OR bp.contact_phone = ?
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, emailOrPhone);
            ps.setString(2, emailOrPhone);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    @Override
    public boolean updateStatus(int bookingId, BookingStatus status) {

        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());
            ps.setInt(2, bookingId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateETicket(int bookingId, String eTicket) {

        String sql = "UPDATE bookings SET e_ticket_number = ? WHERE booking_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, eTicket);
            ps.setInt(2, bookingId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Booking mapRow(ResultSet rs) throws Exception {

        Booking booking = new Booking();

        booking.setBookingId(rs.getInt("booking_id"));
        booking.setPnr(rs.getString("pnr"));
        booking.setETicketNumber(rs.getString("e_ticket_number"));
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setTotalFare(rs.getDouble("total_fare"));
        booking.setSeatCharges(rs.getDouble("seat_charges"));
        booking.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
        booking.setExpiryTime(rs.getObject("expiry_time", java.time.LocalDateTime.class));
        booking.setCheckInStatus(rs.getBoolean("check_in_status"));

        return booking;
    }
    @Override
    public boolean updateFlightAndFare(int bookingId, int flightId, double totalFare, double seatCharges) {

        String sql = """
                UPDATE bookings
                SET flight_id = ?, total_fare = ?, seat_charges = ?
                WHERE booking_id = ?
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, flightId);
            ps.setDouble(2, totalFare);
            ps.setDouble(3, seatCharges);
            ps.setInt(4, bookingId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    @Override
    public List<Booking> findAll() {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                bookings.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }
}