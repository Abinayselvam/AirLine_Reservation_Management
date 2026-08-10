package examples.integration;

import java.util.Map;

public class PaymentGatewayRouter {

    private final Map<String, IPaymentGatewayClient> gateways = Map.of(
            "RAZORPAY", new RazorpayClient(),
            "PAYU", new PayUClient(),
            "CCAVENUE", new CCAvenueClient()
    );

    private final String primaryGateway;

    public PaymentGatewayRouter(String primaryGateway) {
        this.primaryGateway = primaryGateway.toUpperCase();
    }

    /** Routes to the configured primary gateway; falls back to the next one if it fails. */
    public GatewayResponse route(double amount, String reference) {

        IPaymentGatewayClient primary = gateways.get(primaryGateway);

        GatewayResponse response = primary.charge(amount, reference);

        if (response.isSuccess()) {
            return response;
        }

        for (var entry : gateways.entrySet()) {

            if (entry.getKey().equals(primaryGateway)) continue;

            GatewayResponse fallback = entry.getValue().charge(amount, reference);

            if (fallback.isSuccess()) {

                System.out.println(primary.gatewayName() + " failed, routed to " +
                        entry.getValue().gatewayName());

                return fallback;
            }
        }

        return response; // all failed - return the primary's failure
    }
}