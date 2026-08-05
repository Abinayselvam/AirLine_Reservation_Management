package examples.notification;

public class WhatsAppNotification extends Notification {

    public WhatsAppNotification(String recipient, String subject, String message) {
        super(recipient, subject, message);
    }

    @Override
    public boolean send() {

        if (recipient == null || recipient.isBlank()) {

            System.out.println("[WhatsApp] Failed - invalid number");

            return false;
        }

        System.out.println("[WhatsApp -> " + recipient + "] " + subject + " | " + message);

        return true;
    }
}