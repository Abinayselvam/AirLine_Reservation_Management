package examples.util;

import examples.model.User;

public class SessionManager
{
    private static User loggedInUser;
    private static java.time.LocalDateTime lastActivity;

    private static final long SESSION_TIMEOUT_MINUTES = 30;

    public static void touch() {
        lastActivity = java.time.LocalDateTime.now();
    }

    public static void requireActiveSession() {

        if (getLoggedInUser() == null) {
            throw new examples.exception.ExpiredSessionException();
        }

        if (lastActivity != null &&
                java.time.Duration.between(lastActivity, java.time.LocalDateTime.now()).toMinutes()
                        > SESSION_TIMEOUT_MINUTES) {

            logout();

            throw new examples.exception.ExpiredSessionException();
        }

        touch();
    }

    public static void login(User user)
    {
        loggedInUser = user;
    }

    public static User getLoggedInUser()
    {
        return loggedInUser;
    }

    public static void logout()
    {
        loggedInUser = null;
    }
}
