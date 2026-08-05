package examples.manager;

import examples.enums.PaymentStatus;
import examples.model.PaymentResult;
import examples.model.PaymentTransaction;
import examples.model.RefundResult;
import examples.payment.IPayment;
import examples.repository.PaymentRepository;
import examples.repository.irepository.IPaymentRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentManager {

    private static volatile PaymentManager instance;

    private final IPaymentRepository repository = new PaymentRepository();

    private final List<PaymentTransaction> transactionLog =
            Collections.synchronizedList(new ArrayList<>());

    private PaymentManager() {}

    public static PaymentManager getInstance() {

        if (instance == null) {

            synchronized (PaymentManager.class) {

                if (instance == null) {
                    instance = new PaymentManager();
                }
            }
        }

        return instance;
    }

    /** Routes a payment through whichever Payment implementation the caller built (UPI/Card/EMI). */
    public PaymentResult routePayment(IPayment handler, double amount) {
        return handler.process(amount);
    }

    public RefundResult routeRefund(IPayment handler, String reference, double amount) {
        return handler.refund(reference, amount);
    }

    public void logTransaction(PaymentTransaction txn) {

        repository.save(txn);

        transactionLog.add(txn);
    }

    /** Stand-in for a real gateway webhook endpoint updating a transaction after the fact. */
    public void handleWebhookCallback(String gatewayTransactionId, boolean success) {

        synchronized (transactionLog) {

            transactionLog.stream()
                    .filter(t -> t.getGatewayTransactionId().equals(gatewayTransactionId))
                    .findFirst()
                    .ifPresent(t -> {

                        PaymentStatus newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

                        t.setStatus(newStatus);

                        repository.updateStatus(t.getTransactionId(), newStatus);

                        System.out.println("Webhook processed for " + gatewayTransactionId +
                                " -> " + newStatus);
                    });
        }
    }

    public List<PaymentTransaction> getTransactionLog() {

        synchronized (transactionLog) {
            return new ArrayList<>(transactionLog);
        }
    }

    public int logSize() {
        return transactionLog.size();
    }
}