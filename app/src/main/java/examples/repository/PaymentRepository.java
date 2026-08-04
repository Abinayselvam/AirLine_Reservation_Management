package examples.repository;

import examples.enums.PaymentMethod;
import examples.enums.PaymentStatus;
import examples.model.PaymentTransaction;
import examples.repository.irepository.IPaymentRepository;
import examples.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository implements IPaymentRepository {

    @Override
    public boolean save(PaymentTransaction txn) {

        String sql = """
                INSERT INTO payment_transactions
                (booking_id, gateway_transaction_id, method, amount,
                 discount_applied, status, created_at)
                VALUES (?,?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, txn.getBookingId());
            ps.setString(2, txn.getGatewayTransactionId());
            ps.setString(3, txn.getMethod().name());
            ps.setDouble(4, txn.getAmount());
            ps.setDouble(5, txn.getDiscountApplied());
            ps.setString(6, txn.getStatus().name());
            ps.setObject(7, txn.getCreatedAt());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<PaymentTransaction> findByBookingId(int bookingId) {

        List<PaymentTransaction> list = new ArrayList<>();

        String sql = "SELECT * FROM payment_transactions WHERE booking_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, bookingId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                PaymentTransaction txn = new PaymentTransaction();

                txn.setTransactionId(rs.getInt("transaction_id"));
                txn.setBookingId(rs.getInt("booking_id"));
                txn.setGatewayTransactionId(rs.getString("gateway_transaction_id"));
                txn.setMethod(PaymentMethod.valueOf(rs.getString("method")));
                txn.setAmount(rs.getDouble("amount"));
                txn.setDiscountApplied(rs.getDouble("discount_applied"));
                txn.setStatus(PaymentStatus.valueOf(rs.getString("status")));
                txn.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));

                list.add(txn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean updateStatus(int transactionId, PaymentStatus status) {

        String sql = "UPDATE payment_transactions SET status = ? WHERE transaction_id = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());
            ps.setInt(2, transactionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}