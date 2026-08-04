package examples.payment;

import examples.model.PaymentResult;
import examples.model.RefundResult;

public interface IPayment {

    boolean validate();

    PaymentResult process(double amount);

    RefundResult refund(String transactionId, double amount);
}