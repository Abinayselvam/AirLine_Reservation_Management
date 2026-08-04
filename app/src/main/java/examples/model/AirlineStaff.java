package examples.model;

import examples.enums.Permission;

import java.util.Set;

public class AirlineStaff extends User {

    @Override
    public Set<Permission> getPermissions() {

        return Set.of(
                Permission.MANAGE_FLIGHTS,
                Permission.VIEW_ALL_BOOKINGS
        );
    }

    @Override
    public void showDashboard() {

        System.out.println("""
            ===== Airline Staff Dashboard =====

            1.Update Flight

            2.View Bookings

            """);
    }
}