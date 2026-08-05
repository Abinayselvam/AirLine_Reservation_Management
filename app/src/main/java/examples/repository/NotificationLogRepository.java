package examples.repository;

import examples.enums.NotificationChannel;
import examples.enums.NotificationType;
import examples.model.NotificationLog;
import examples.repository.irepository.INotificationLogRepository;
import examples.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationLogRepository implements INotificationLogRepository {

    @Override
    public boolean save(NotificationLog log) {

        String sql = """
                INSERT INTO notification_logs
                (recipient, channel, type, message, success, sent_at)
                VALUES (?,?,?,?,?,?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, log.getRecipient());
            ps.setString(2, log.getChannel().name());
            ps.setString(3, log.getType().name());
            ps.setString(4, log.getMessage());
            ps.setBoolean(5, log.isSuccess());
            ps.setObject(6, log.getSentAt());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<NotificationLog> findAll() {

        List<NotificationLog> logs = new ArrayList<>();

        String sql = "SELECT * FROM notification_logs ORDER BY sent_at DESC";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                NotificationLog log = new NotificationLog();

                log.setLogId(rs.getInt("log_id"));
                log.setRecipient(rs.getString("recipient"));
                log.setChannel(NotificationChannel.valueOf(rs.getString("channel")));
                log.setType(NotificationType.valueOf(rs.getString("type")));
                log.setMessage(rs.getString("message"));
                log.setSuccess(rs.getBoolean("success"));
                log.setSentAt(rs.getObject("sent_at", java.time.LocalDateTime.class));

                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }
}