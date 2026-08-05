package examples.service;

import examples.enums.PaymentMethod;
import examples.enums.PaymentStatus;
import examples.manager.PaymentManager;
import examples.model.*;
import examples.payment.CardPayment;
import examples.payment.EmiPayment;
import examples.payment.IPayment;
import examples.payment.UpiPayment;
import examples.repository.BookingRepository;
import examples.repository.irepository.IBookingRepository;
import examples.repository.irepository.IPaymentRepository;
import examples.repository.PaymentRepository;
import examples.service.iservice.IPaymentService;
import examples.util.PromoCodeUtil;

import java.time.LocalDateTime;
import java.util.Scanner;

public class PaymentService implements IPaymentService {

    private static final int MAX_ATTEMPTS = 3;

    private final Scanner sc = new Scanner(System.in);

    private final IPaymentRepository paymentRepository = new PaymentRepository();

    private final IBookingRepository bookingRepository = new BookingRepository();

    @Override
    public boolean processPayment(Booking booking) {

        System.out.println("\n===== FARE SUMMARY =====");
        System.out.printf("Base + Seat Charges : Rs.%.2f%n", booking.getTotalFare());

        System.out.print("Promo Code (blank if none) : ");

        String promo = sc.nextLine();

        double discountRate = PromoCodeUtil.discountFor(promo);

        double discount = booking.getTotalFare() * discountRate;

        double payable = booking.getTotalFare() - discount;

        if (discount > 0) {
            System.out.printf("Discount Applied (%s) : -Rs.%.2f%n", promo.toUpperCase(), discount);
        }

        System.out.printf("Amount Payable : Rs.%.2f%n", payable);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            PaymentMethod method = selectMethod();

            IPayment payment = buildPaymentHandler(method);

            if (payment == null) {

                System.out.println("Invalid method selected");

                continue;
            }

            PaymentResult result = examples.manager.PaymentManager.getInstance().routePayment(payment, payable);

            PaymentTransaction txn = new PaymentTransaction();

            txn.setBookingId(booking.getBookingId());
            txn.setGatewayTransactionId("TXN" + System.currentTimeMillis());
            txn.setMethod(method);
            txn.setAmount(payable);
            txn.setDiscountApplied(discount);
            txn.setCreatedAt(LocalDateTime.now());
            txn.setStatus(result.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

            PaymentManager.getInstance().logTransaction(txn);

            if (result.isSuccess()) {

                System.out.println(result.getMessage());

                printReceipt(booking, txn);
                examples.manager.NotificationManager.getInstance()
                        .sendPaymentReceipt(new examples.repository.UserRepository().findById(booking.getUserId()),
                                booking.getPnr(), txn.getAmount());

                return true;
            }

            System.out.println("Payment Failed : " + result.getMessage());

            if (attempt < MAX_ATTEMPTS) {
                System.out.println("Retrying (" + attempt + "/" + MAX_ATTEMPTS + ")");
            }
        }

        System.out.println("Payment failed after " + MAX_ATTEMPTS + " attempts");

        return false;
    }

    private PaymentMethod selectMethod() {

        System.out.print("Payment Method [1-UPI 2-Card 3-EMI] : ");

        int choice = Integer.parseInt(sc.nextLine());

        return switch (choice) {
            case 1 -> PaymentMethod.UPI;
            case 2 -> PaymentMethod.CARD;
            case 3 -> PaymentMethod.EMI;
            default -> null;
        };
    }

    private IPayment buildPaymentHandler(PaymentMethod method) {

        if (method == null) {
            return null;
        }

        return switch (method) {

            case UPI -> {

                System.out.print("Enter UPI ID (e.g. name@okhdfcbank) : ");

                yield new UpiPayment(new UpiDetails(sc.nextLine()));
            }

            case CARD -> new CardPayment(collectCardDetails());

            case EMI -> {

                CardDetails details = collectCardDetails();

                System.out.print("EMI Tenure (3/6/9/12 months) : ");

                int tenure = Integer.parseInt(sc.nextLine());

                yield new EmiPayment(details, tenure);
            }
        };
    }

    private CardDetails collectCardDetails() {

        CardDetails details = new CardDetails();

        System.out.print("Card Number : ");
        details.setCardNumber(sc.nextLine().replaceAll("\\s", ""));

        System.out.print("Cardholder Name : ");
        details.setCardHolderName(sc.nextLine());

        System.out.print("Expiry Month (1-12) : ");
        details.setExpiryMonth(Integer.parseInt(sc.nextLine()));

        System.out.print("Expiry Year (yyyy) : ");
        details.setExpiryYear(Integer.parseInt(sc.nextLine()));

        System.out.print("CVV : ");
        details.setCvv(sc.nextLine());

        return details;
    }

    private void printReceipt(Booking booking, PaymentTransaction txn) {

        System.out.println("\n===== PAYMENT RECEIPT =====");
        System.out.println("Transaction ID : " + txn.getGatewayTransactionId());
        System.out.println("PNR : " + booking.getPnr());
        System.out.println("Method : " + txn.getMethod());
        System.out.printf("Amount Paid : Rs.%.2f%n", txn.getAmount());
        System.out.println("Status : " + txn.getStatus());
        System.out.println("Date : " + txn.getCreatedAt());
    }

    @Override
    public void processStandaloneRefund() {

        System.out.print("Enter PNR to refund : ");

        Booking booking = bookingRepository.findByPNR(sc.nextLine());

        if (booking == null) {

            System.out.println("Booking not found");

            return;
        }

        System.out.print("Refund Amount : ");

        double amount = Double.parseDouble(sc.nextLine());


        RefundResult result = processRefund(booking.getBookingId(), booking.getPnr(), amount);

        System.out.println(result.getMessage());
        PaymentTransaction txn = new PaymentTransaction();

        txn.setBookingId(booking.getBookingId());
        txn.setGatewayTransactionId(result.getRefundTransactionId());
        txn.setMethod(PaymentMethod.UPI);
        txn.setAmount(amount);
        txn.setStatus(result.isSuccess() ? PaymentStatus.REFUNDED : PaymentStatus.REFUND_FAILED);
        txn.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(txn);

        System.out.println(result.getMessage());
    }


    public RefundResult processRefund(int bookingId, String pnr, double amount) {

        UpiPayment refundHandler = new UpiPayment(new UpiDetails("refund@bank"));

        RefundResult result = refundHandler.refund(pnr, amount);

        PaymentTransaction txn = new PaymentTransaction();

        txn.setBookingId(bookingId);
        txn.setGatewayTransactionId(result.getRefundTransactionId());
        txn.setMethod(PaymentMethod.UPI);
        txn.setAmount(amount);
        txn.setStatus(result.isSuccess() ? PaymentStatus.REFUNDED : PaymentStatus.REFUND_FAILED);
        txn.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(txn);


        return result;
    }

    public RefundResult processRefund(int userId, int bookingId, String pnr, double amount) {

        RefundResult result = processRefund(bookingId, pnr, amount);

        examples.manager.NotificationManager.getInstance().sendRefundInitiated(
                new examples.repository.UserRepository().findById(userId), pnr, amount);

        return result;
    }

}