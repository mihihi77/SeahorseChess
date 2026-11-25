
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String USER = "seahorse_app";

    public static Connection getConnection() throws SQLException {

        String url = System.getenv("DB_URL");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || url.isBlank()) {
            url = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
        }

        if (password == null || password.isBlank()) {
            throw new SQLException("DB_PASSWORD is not set.");
        }

        return DriverManager.getConnection(url, USER, password);
    }
}
