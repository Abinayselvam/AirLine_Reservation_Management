package examples.repository;

import examples.enums.SeatCategory;
import examples.enums.SeatStatus;
import examples.enums.SeatType;
import examples.model.Seat;
import examples.repository.irepository.ISeatRepository;
import examples.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SeatRepository implements ISeatRepository {

    @Override
    public List<Seat> findByFlightId(int flightId) {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats WHERE flight_id = ? ORDER BY seat_id";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, flightId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                seats.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seats;
    }

    @Override
    public boolean saveAll(List<Seat> seats) {

        String sql = """
                INSERT INTO seats
                (flight_id, seat_number, seat_type, category, status,
                 extra_charge, power_outlet, extra_legroom)
                VALUES (?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            for (Seat seat : seats) {

                ps.setInt(1, seat.getFlightId());
                ps.setString(2, seat.getSeatNumber());
                ps.setString(3, seat.getSeatType().name());
                ps.setString(4, seat.getCategory().name());
                ps.setString(5, seat.getStatus().name());
                ps.setDouble(6, seat.getExtraCharge());
                ps.setBoolean(7, seat.isPowerOutlet());
                ps.setBoolean(8, seat.isExtraLegroom());

                ps.addBatch();
            }

            int[] results = ps.executeBatch();

            return results.length == seats.size();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateStatus(int seatId, SeatStatus status) {

        String sql = "UPDATE seats SET status = ? WHERE seat_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());
            ps.setInt(2, seatId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Seat findBySeatNumber(int flightId, String seatNumber) {

        String sql = "SELECT * FROM seats WHERE flight_id = ? AND seat_number = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, flightId);
            ps.setString(2, seatNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Seat mapRow(ResultSet rs) throws Exception {

        Seat seat = new Seat();

        seat.setSeatId(rs.getInt("seat_id"));
        seat.setFlightId(rs.getInt("flight_id"));
        seat.setSeatNumber(rs.getString("seat_number"));
        seat.setSeatType(SeatType.valueOf(rs.getString("seat_type")));
        seat.setCategory(SeatCategory.valueOf(rs.getString("category")));
        seat.setStatus(SeatStatus.valueOf(rs.getString("status")));
        seat.setExtraCharge(rs.getDouble("extra_charge"));
        seat.setPowerOutlet(rs.getBoolean("power_outlet"));
        seat.setExtraLegroom(rs.getBoolean("extra_legroom"));

        return seat;
    }
}