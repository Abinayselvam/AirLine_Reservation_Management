package examples.payment;

import examples.model.PaymentResult;
import examples.model.RefundResult;
import examples.model.UpiDetails;
import examples.util.OTPGenerator;

import java.util.Scanner;
import java.util.regex.Pattern;

public class UpiPayment implements IPayment {

    private static final Pattern UPI_PATTERN =
            Pattern.compile("^[\\w.\\-]{2,256}@[a-zA-Z]{2,64}$");

    private final UpiDetails details;

    private final Scanner sc = new Scanner(System.in);

    public UpiPayment(UpiDetails details) {
        this.details = details;
    }

    @Override
    public boolean validate() {

        if (details.getUpiId() == null || !UPI_PATTERN.matcher(details.getUpiId()).matches()) {

            System.out.println("Invalid UPI ID format (expected name@bank)");

            return false;
        }

        return true;
    }

    @Override
    public PaymentResult process(double amount) {

        PaymentResult result = new PaymentResult();

        if (!validate()) {

            result.setSuccess(false);
            result.setMessage("Validation failed");

            return result;
        }

        String otp = String.valueOf(OTPGenerator.generateOTP());

        System.out.println("An OTP has been sent to your UPI app (simulated: " + otp + ")");

        System.out.print("Enter OTP to authorize payment of Rs." + amount + " : ");

        if (!sc.nextLine().equals(otp)) {

            result.setSuccess(false);
            result.setMessage("OTP mismatch - payment declined");

            return result;
        }

        result.setSuccess(true);
        result.setMessage("Payment successful via UPI (" + details.getUpiId() + ")");
        result.setAmount(amount);

        return result;
    }

    @Override
    public RefundResult refund(String transactionId, double amount) {

        RefundResult result = new RefundResult();

        result.setSuccess(true);
        result.setRefundTransactionId("RFND" + System.currentTimeMillis());
        result.setAmount(amount);
        result.setMessage("Refund of Rs." + amount + " initiated to UPI ID " + details.getUpiId());

        return result;
    }
}