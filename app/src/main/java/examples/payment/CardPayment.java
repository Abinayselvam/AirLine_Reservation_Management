package examples.payment;

import examples.model.CardDetails;
import examples.model.PaymentResult;
import examples.model.RefundResult;
import examples.util.CardValidationUtil;
import examples.util.OTPGenerator;

import java.util.Scanner;

public class CardPayment implements IPayment {

    protected final CardDetails details;

    protected final Scanner sc = new Scanner(System.in);

    public CardPayment(CardDetails details) {
        this.details = details;
    }

    @Override
    public boolean validate() {

        if (!CardValidationUtil.isValidCardNumber(details.getCardNumber())) {

            System.out.println("Invalid card number");

            return false;
        }

        if (!CardValidationUtil.isValidExpiry(details.getExpiryMonth(), details.getExpiryYear())) {

            System.out.println("Card has expired");

            return false;
        }

        if (!CardValidationUtil.isValidCvv(details.getCvv())) {

            System.out.println("Invalid CVV");

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

        System.out.println("3D Secure OTP sent to your registered mobile (simulated: " + otp + ")");

        System.out.print("Enter OTP to authorize payment of Rs." + amount + " : ");

        if (!sc.nextLine().equals(otp)) {

            result.setSuccess(false);
            result.setMessage("OTP mismatch - payment declined");

            return result;
        }

        result.setSuccess(true);
        result.setMessage("Payment successful via Card ending " + details.maskedNumber());
        result.setAmount(amount);

        return result;
    }

    @Override
    public RefundResult refund(String transactionId, double amount) {

        RefundResult result = new RefundResult();

        result.setSuccess(true);
        result.setRefundTransactionId("RFND" + System.currentTimeMillis());
        result.setAmount(amount);
        result.setMessage("Refund of Rs." + amount + " initiated to card ending " + details.maskedNumber());

        return result;
    }
}