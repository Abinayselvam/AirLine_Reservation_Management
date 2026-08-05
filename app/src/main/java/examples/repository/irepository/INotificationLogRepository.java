package examples.repository.irepository;

import examples.model.NotificationLog;

import java.util.List;

public interface INotificationLogRepository {

    boolean save(NotificationLog log);

    List<NotificationLog> findAll();
}