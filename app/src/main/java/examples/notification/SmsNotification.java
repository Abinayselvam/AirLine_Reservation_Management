package examples.notification;

import examples.integration.ISmsGatewayClient;
import examples.integration.TwilioLikeSmsClient;

public class SmsNotification extends Notification {

    private final ISmsGatewayClient gatewayClient;

    public SmsNotification(String recipient, String subject, String message) {
        super(recipient, subject, message);
        this.gatewayClient = new TwilioLikeSmsClient();
    }

    @Override
    public boolean send() {

        if (recipient == null || recipient.isBlank()) {

            System.out.println("[SMS] Failed - invalid number");

            return false;
        }

        return gatewayClient.sendSms(recipient, subject + ": " + message);
    }
}