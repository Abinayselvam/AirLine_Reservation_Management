package Examples.model;

import javax.management.relation.Role;

public class Passenger extends User {

    public Passenger() {}

    public Passenger(int userId, String name,
                     String email,
                     String phone,
                     String password,
                     String dob,
                     String passport,
                     Role role) {

        super(userId,name,email,phone,password,dob,passport,role);
    }

    @Override
    public void showDashboard() {
        System.out.println("Passenger Dashboard");
    }
}