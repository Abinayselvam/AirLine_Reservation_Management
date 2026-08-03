package Examples.model;

import Examples.model.User;

public class Admin extends User {

    @Override
    public void showDashboard() {
        System.out.println("Admin Dashboard");
    }
}