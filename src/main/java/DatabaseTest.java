
import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) throws SQLException {

        String sql = "SELECT COUNT(*) FROM PLAYER";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            System.out.println("Connected to Oracle successfully!");

            if (rs.next()) {
                System.out.println("Total players: " + rs.getInt(1));
            }
        }
    }
}
