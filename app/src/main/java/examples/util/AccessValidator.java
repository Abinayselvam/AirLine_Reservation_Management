package examples.util;

import examples.enums.Permission;
import examples.model.User;

public final class AccessValidator {

    private AccessValidator() {}

    public static boolean validate(Permission required) {

        User user =
                SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println(
                    "Login Required");

            return false;
        }

        if (!user.hasPermission(required)) {

            System.out.println(
                    "Access Denied : " + user.getRole() +
                            " cannot perform this action");

            return false;
        }

        return true;
    }
}
