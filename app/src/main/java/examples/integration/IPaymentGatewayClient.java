package examples.integration;

public interface IPaymentGatewayClient {

    String gatewayName();

    GatewayResponse charge(double amount, String reference);

    GatewayResponse refund(String gatewayTransactionId, double amount);
}