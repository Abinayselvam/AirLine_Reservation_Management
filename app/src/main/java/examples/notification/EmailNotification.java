package examples.notification;

import examples.integration.IEmailGatewayClient;
import examples.integration.SendGridLikeEmailClient;
public class EmailNotification extends Notification {

    private final IEmailGatewayClient gatewayClient;

    public EmailNotification(String recipient, String subject, String message) {
        super(recipient, subject, message);
        this.gatewayClient = new SendGridLikeEmailClient();
    }

    @Override
    public boolean send() {

        if (recipient == null || !recipient.contains("@")) {

            System.out.println("[Email] Failed - invalid address");

            return false;
        }

        return gatewayClient.sendEmail(recipient, subject, message);
    }
}