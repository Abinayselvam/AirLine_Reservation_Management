package examples.payment;

import examples.model.CardDetails;
import examples.model.PaymentResult;

public class EmiPayment extends CardPayment {

    private static final double MIN_EMI_AMOUNT = 3000;

    private final int tenureMonths;

    public EmiPayment(CardDetails details, int tenureMonths) {
        super(details);
        this.tenureMonths = tenureMonths;
    }

    public boolean isEligible(double amount) {

        if (amount < MIN_EMI_AMOUNT) {

            System.out.println("EMI is only available for amounts above Rs." + MIN_EMI_AMOUNT);

            return false;
        }

        return true;
    }

    @Override
    public PaymentResult process(double amount) {

        if (!isEligible(amount)) {

            PaymentResult result = new PaymentResult();

            result.setSuccess(false);
            result.setMessage("Not eligible for EMI");

            return result;
        }

        double perMonth = amount / tenureMonths;

        System.out.printf("EMI Plan : %d months x Rs.%.2f%n", tenureMonths, perMonth);

        return super.process(amount);
    }
}