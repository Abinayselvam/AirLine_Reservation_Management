package examples.integration;

public class SendGridLikeEmailClient implements IEmailGatewayClient {

    @Override
    public boolean sendEmail(String toAddress, String subject, String body) {

        // Real integration point: SendGrid/SES REST API call - no live credentials here.
        System.out.println("[Email Gateway -> " + toAddress + "] " + subject);

        return true;
    }
}