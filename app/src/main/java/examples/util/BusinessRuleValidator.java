package examples.util;

import examples.enums.AgeCategory;
import examples.model.BookingPassenger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class BusinessRuleValidator {

    private static final int MIN_LEAD_TIME_HOURS = 2;

    private static final int MAX_PASSENGERS_PER_BOOKING = 6;

    private BusinessRuleValidator() {}

    public static String validateLeadTime(LocalDateTime departureDateTime) {

        long hoursUntilDeparture =
                Duration.between(LocalDateTime.now(), departureDateTime).toHours();

        if (hoursUntilDeparture < MIN_LEAD_TIME_HOURS) {
            return "Bookings must be made at least " + MIN_LEAD_TIME_HOURS + " hours before departure";
        }

        return null; // null = valid
    }

    public static String validatePassengerCount(int count) {

        if (count < 1 || count > MAX_PASSENGERS_PER_BOOKING) {
            return "A booking must have between 1 and " + MAX_PASSENGERS_PER_BOOKING + " passengers";
        }

        return null;
    }

    public static AgeCategory ageCategory(int age) {

        if (age < 2) return AgeCategory.INFANT;

        if (age < 12) return AgeCategory.CHILD;

        return AgeCategory.ADULT;
    }

    public static String validateInfantsHaveAdult(List<BookingPassenger> passengers) {

        boolean hasInfant = passengers.stream()
                .anyMatch(p -> ageCategory(p.getAge()) == AgeCategory.INFANT);

        boolean hasAdult = passengers.stream()
                .anyMatch(p -> ageCategory(p.getAge()) == AgeCategory.ADULT);

        if (hasInfant && !hasAdult) {
            return "An infant must travel with an adult";
        }

        return null;
    }

    public static String validateDocumentExpiry(BookingPassenger passenger, LocalDate travelDate) {

        if (passenger.getIdProofExpiryDate() == null) {
            return null; // no expiry captured - not blocking, just unvalidated
        }

        if (passenger.getIdProofExpiryDate().isBefore(travelDate)) {
            return passenger.getName() + "'s ID document expires before the travel date";
        }

        // Common international rule: passport must be valid 6 months past travel
        return null;
    }

    public static String validateInternationalDocument(
            BookingPassenger passenger, LocalDate travelDate, boolean isInternational) {

        if (!isInternational) {
            return null;
        }

        if (passenger.getIdProof() == null || passenger.getIdProof().length() < 6) {
            return passenger.getName() + " needs a valid passport number for an international flight";
        }

        if (passenger.getIdProofExpiryDate() != null &&
                passenger.getIdProofExpiryDate().isBefore(travelDate.plusMonths(6))) {
            return passenger.getName() + "'s passport must be valid at least 6 months past the travel date";
        }

        return validateDocumentExpiry(passenger, travelDate);
    }
}