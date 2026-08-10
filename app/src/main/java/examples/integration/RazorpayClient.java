package examples.integration;

public class RazorpayClient implements IPaymentGatewayClient {

    @Override
    public String gatewayName() { return "Razorpay"; }

    @Override
    public GatewayResponse charge(double amount, String reference) {

        // Real integration point: POST to https://api.razorpay.com/v1/payments
        // using RAZORPAY_KEY_ID / RAZORPAY_KEY_SECRET - not called here, no live credentials.
        String txnId = "RZP" + System.currentTimeMillis();

        return new GatewayResponse(true, txnId, "Simulated Razorpay charge for " + reference);
    }

    @Override
    public GatewayResponse refund(String gatewayTransactionId, double amount) {

        return new GatewayResponse(true, "RZPR" + System.currentTimeMillis(),
                "Simulated Razorpay refund for " + gatewayTransactionId);
    }
}