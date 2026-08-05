package examples.notification;

public class SmsNotification extends Notification {

    public SmsNotification(String recipient, String subject, String message) {
        super(recipient, subject, message);
    }

    @Override
    public boolean send() {

        if (recipient == null || recipient.isBlank()) {

            System.out.println("[SMS] Failed - invalid number");

            return false;
        }

        // SMS is short-form: subject folded into the message body
        System.out.println("[SMS -> " + recipient + "] " + subject + ": " + message);

        return true;
    }
}