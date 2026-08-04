package examples.service.iservice;

import examples.model.Booking;
import examples.model.RefundResult;

public interface IPaymentService {

    boolean processPayment(Booking booking);

    void processStandaloneRefund();

    RefundResult processRefund(int bookingId, String pnr, double amount);
}