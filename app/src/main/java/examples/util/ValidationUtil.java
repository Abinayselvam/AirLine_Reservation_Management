package examples.util;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {

        return email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidPhone(String phone) {

        return phone.matches("[6-9][0-9]{9}");
    }

    public static boolean isValidPassport(String passport) {

        return passport.matches("[A-Z][0-9]{7}");
    }

}
