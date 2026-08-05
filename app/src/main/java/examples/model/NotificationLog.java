package examples.model;

import examples.enums.NotificationChannel;
import examples.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationLog {

    private int logId;
    private String recipient;
    private NotificationChannel channel;
    private NotificationType type;
    private String message;
    private boolean success;
    private LocalDateTime sentAt;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}