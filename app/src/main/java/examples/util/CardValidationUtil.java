package examples.util;

import java.time.YearMonth;

public final class CardValidationUtil {

    private CardValidationUtil() {}

    public static boolean isValidCardNumber(String number) {

        if (number == null || !number.matches("\\d{13,19}")) {
            return false;
        }

        return luhnCheck(number);
    }

    private static boolean luhnCheck(String number) {

        int sum = 0;

        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {

            int digit = Character.getNumericValue(number.charAt(i));

            if (alternate) {

                digit *= 2;

                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;

            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    public static boolean isValidExpiry(int month, int year) {

        if (month < 1 || month > 12) {
            return false;
        }

        return !YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    public static boolean isValidCvv(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }
}