package examples.exception;

public class PaymentFailedException extends AirlineSystemException {

    public PaymentFailedException(String reason) {
        super("PAYMENT_FAILED", "Payment failed: " + reason);
    }
}