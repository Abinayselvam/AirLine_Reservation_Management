package examples.model;

import examples.enums.Permission;

import java.util.Set;

public class Admin extends User {

    @Override
    public Set<Permission> getPermissions() {

        return Set.of(
                Permission.MANAGE_ALL_BOOKINGS,
                Permission.MANAGE_ALL_USERS,
                Permission.MANAGE_FLIGHTS,
                Permission.VIEW_ALL_BOOKINGS,
                Permission.VIEW_REPORTS,
                Permission.MANAGE_AIRPORTS

        );
    }

    @Override
    public void showDashboard() {

        System.out.println("""
            ===== Admin Dashboard =====

            1.Manage Flights

            2.Manage Users

            3.Reports

            4.Manage Airports
            
            5.Priority Bookings

            """);
    }
}