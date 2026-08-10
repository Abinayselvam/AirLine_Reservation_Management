package examples.integration;

public class GatewayResponse {

    private final boolean success;
    private final String gatewayTransactionId;
    private final String message;

    public GatewayResponse(boolean success, String gatewayTransactionId, String message) {
        this.success = success;
        this.gatewayTransactionId = gatewayTransactionId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public String getMessage() { return message; }
}