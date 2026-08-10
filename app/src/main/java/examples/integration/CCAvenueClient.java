package examples.integration;

public class CCAvenueClient implements IPaymentGatewayClient {

    @Override
    public String gatewayName() { return "CCAvenue"; }

    @Override
    public GatewayResponse charge(double amount, String reference) {

        String txnId = "CCAV" + System.currentTimeMillis();

        return new GatewayResponse(true, txnId, "Simulated CCAvenue charge for " + reference);
    }

    @Override
    public GatewayResponse refund(String gatewayTransactionId, double amount) {

        return new GatewayResponse(true, "CCAVR" + System.currentTimeMillis(),
                "Simulated CCAvenue refund for " + gatewayTransactionId);
    }
}