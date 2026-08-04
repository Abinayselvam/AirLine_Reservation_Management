package examples.model;

import examples.enums.Permission;
import examples.enums.Role;

import java.util.Set;

public class Passenger extends User {

    public Passenger() {}

    public Passenger(int userId, String name,
                     String email,
                     String phone,
                     String password,
                     String dob,
                     String passport,
                     Role role, boolean active) {

        super(userId,name,email,phone,password,dob,passport,role,active);
    }

    @Override
    public Set<Permission> getPermissions() {

        return Set.of(
                Permission.MANAGE_OWN_BOOKINGS
        );
    }

    @Override
    public void showDashboard() {

        System.out.println("""
            ===== Passenger Dashboard =====

            1.Search Flight

            2.My Bookings

            3.Profile

            """);
    }
}