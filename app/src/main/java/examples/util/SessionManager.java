package examples.util;

import examples.model.User;

public class SessionManager
{
    private static User loggedInUser;

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
