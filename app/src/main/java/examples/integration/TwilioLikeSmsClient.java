package examples.integration;

public class TwilioLikeSmsClient implements ISmsGatewayClient {

    @Override
    public boolean sendSms(String toNumber, String message) {

        // Real integration point: Twilio/MSG91 REST API call - no live credentials here.
        System.out.println("[SMS Gateway -> " + toNumber + "] " + message);

        return true;
    }
}