package examples.integration;

public interface IEmailGatewayClient {
    boolean sendEmail(String toAddress, String subject, String body);
}