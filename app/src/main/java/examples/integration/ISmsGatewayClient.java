package examples.integration;

public interface ISmsGatewayClient {
    boolean sendSms(String toNumber, String message);
}