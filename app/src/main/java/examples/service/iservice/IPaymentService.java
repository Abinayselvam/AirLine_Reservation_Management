package examples.service.iservice;

import examples.model.Booking;

public interface IPaymentService {

    boolean processPayment(Booking booking);

    void processStandaloneRefund();
}