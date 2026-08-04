package examples.service;

import examples.model.Booking;

public interface IPaymentService {

    boolean processPayment(Booking booking);

    void processStandaloneRefund();
}