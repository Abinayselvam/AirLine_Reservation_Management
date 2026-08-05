package examples.notification;

public class EmailNotification extends Notification {

    public EmailNotification(String recipient, String subject, String message) {
        super(recipient, subject, message);
    }

    @Override
    public boolean send() {

        if (recipient == null || !recipient.contains("@")) {

            System.out.println("[Email] Failed - invalid address");

            return false;
        }

        System.out.println("[Email -> " + recipient + "] " + subject + " | " + message);

        return true;
    }
}