package examples.manager;

import examples.enums.CommunicationPreference;
import examples.enums.NotificationChannel;
import examples.enums.NotificationType;
import examples.model.NotificationLog;
import examples.model.User;
import examples.notification.EmailNotification;
import examples.notification.Notification;
import examples.notification.SmsNotification;
import examples.notification.WhatsAppNotification;
import examples.repository.NotificationLogRepository;
import examples.repository.irepository.INotificationLogRepository;

import java.time.LocalDateTime;

public class NotificationManager {

    private static volatile NotificationManager instance;

    private final INotificationLogRepository logRepository = new NotificationLogRepository();

    private NotificationManager() {}

    public static NotificationManager getInstance() {

        if (instance == null) {

            synchronized (NotificationManager.class) {

                if (instance == null) {
                    instance = new NotificationManager();
                }
            }
        }

        return instance;
    }

    /** Dispatches to Email and/or SMS per the user's stored CommunicationPreference. */
    public void notifyUser(User user, NotificationType type, String subject, String message) {

        if (user == null || user.getCommunicationPreference() == CommunicationPreference.NONE) {
            return;
        }

        CommunicationPreference pref = user.getCommunicationPreference();

        if (pref == CommunicationPreference.EMAIL || pref == CommunicationPreference.BOTH) {
            dispatch(new EmailNotification(user.getEmail(), subject, message),
                    NotificationChannel.EMAIL, type);
        }

        if (pref == CommunicationPreference.SMS || pref == CommunicationPreference.BOTH) {
            dispatch(new SmsNotification(user.getPhone(), subject, message),
                    NotificationChannel.SMS, type);
        }
    }

    /** WhatsApp is opt-in per send, per the doc's "optional" note - not tied to CommunicationPreference. */
    public void notifyWhatsApp(String phone, NotificationType type, String subject, String message) {
        dispatch(new WhatsAppNotification(phone, subject, message), NotificationChannel.WHATSAPP, type);
    }

    private void dispatch(Notification notification, NotificationChannel channel, NotificationType type) {

        boolean success = notification.send();

        NotificationLog log = new NotificationLog();

        log.setRecipient(notification.getRecipient());
        log.setChannel(channel);
        log.setType(type);
        log.setMessage(notification.toString());
        log.setSuccess(success);
        log.setSentAt(LocalDateTime.now());

        logRepository.save(log);
    }

    // ---------- Named convenience methods, one per doc bullet ----------

    public void sendBookingConfirmation(User user, String pnr, String eTicket) {

        notifyUser(user, NotificationType.BOOKING_CONFIRMATION,
                "Booking Confirmed - " + pnr,
                "Your booking " + pnr + " is confirmed. E-Ticket : " + eTicket);
    }

    public void sendPaymentReceipt(User user, String pnr, double amount) {

        notifyUser(user, NotificationType.PAYMENT_RECEIPT,
                "Payment Receipt - " + pnr,
                String.format("Payment of Rs.%.2f received for booking %s", amount, pnr));
    }

    public void sendFlightDelay(User user, String flightNumber, String newTime) {

        notifyUser(user, NotificationType.FLIGHT_DELAY,
                "Flight Delayed - " + flightNumber,
                "Your flight " + flightNumber + " is delayed. New departure : " + newTime);
    }

    public void sendGateChange(User user, String flightNumber, String newGate) {

        notifyUser(user, NotificationType.GATE_CHANGE,
                "Gate Change - " + flightNumber,
                "Flight " + flightNumber + " will now depart from gate " + newGate);
    }

    public void sendFlightCancellation(User user, String flightNumber) {

        notifyUser(user, NotificationType.FLIGHT_CANCELLATION,
                "Flight Cancelled - " + flightNumber,
                "We regret to inform you that flight " + flightNumber + " has been cancelled");
    }

    public void sendCheckInReminder(User user, String pnr) {

        notifyUser(user, NotificationType.CHECKIN_REMINDER,
                "Check-In Open - " + pnr,
                "Online check-in is now open for your booking " + pnr);
    }

    public void sendBoardingReminder(User user, String pnr, String gate) {

        notifyUser(user, NotificationType.BOARDING_REMINDER,
                "Boarding Soon - " + pnr,
                "Boarding for your flight starts soon at gate " + gate);
    }

    public void sendModificationConfirmation(User user, String pnr, String eTicket) {

        notifyUser(user, NotificationType.MODIFICATION_CONFIRMATION,
                "Booking Modified - " + pnr,
                "Your booking " + pnr + " has been updated. Revised E-Ticket : " + eTicket);
    }

    public void sendCancellationConfirmation(User user, String pnr) {

        notifyUser(user, NotificationType.CANCELLATION_CONFIRMATION,
                "Booking Cancelled - " + pnr,
                "Your booking " + pnr + " has been cancelled");
    }

    public void sendRefundInitiated(User user, String pnr, double amount) {

        notifyUser(user, NotificationType.REFUND_INITIATED,
                "Refund Initiated - " + pnr,
                String.format("A refund of Rs.%.2f has been initiated for booking %s", amount, pnr));
    }

    public void sendRefundCompleted(User user, String pnr, double amount) {

        notifyUser(user, NotificationType.REFUND_COMPLETED,
                "Refund Completed - " + pnr,
                String.format("Rs.%.2f has been credited back for booking %s", amount, pnr));
    }
}