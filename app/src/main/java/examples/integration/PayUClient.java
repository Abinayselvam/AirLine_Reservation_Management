package examples.integration;

public class PayUClient implements IPaymentGatewayClient {

    @Override
    public String gatewayName() { return "PayU"; }

    @Override
    public GatewayResponse charge(double amount, String reference) {

        String txnId = "PAYU" + System.currentTimeMillis();

        return new GatewayResponse(true, txnId, "Simulated PayU charge for " + reference);
    }

    @Override
    public GatewayResponse refund(String gatewayTransactionId, double amount) {

        return new GatewayResponse(true, "PAYUR" + System.currentTimeMillis(),
                "Simulated PayU refund for " + gatewayTransactionId);
    }
}