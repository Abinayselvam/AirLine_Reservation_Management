package examples.repository.irepository;

import examples.enums.PaymentStatus;
import examples.model.PaymentTransaction;

import java.util.List;

public interface IPaymentRepository {

    boolean save(PaymentTransaction transaction);

    List<PaymentTransaction> findByBookingId(int bookingId);

    boolean updateStatus(int transactionId, PaymentStatus status);

    List<PaymentTransaction> findAll();
}