package examples.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/airline_service";

    private static final String USER = "root";

    private static final String PASSWORD = "root";

    private DBConnection() {
    }

    public static Connection getConnection() {

        try {

            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Unable to connect database",
                    e
            );
        }
    }
}
