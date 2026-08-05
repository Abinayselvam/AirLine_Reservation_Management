package examples.notification;

public abstract class Notification {

    protected String recipient;

    protected String subject;

    protected String message;

    public Notification(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    public abstract boolean send();

    public String getRecipient() { return recipient; }
}