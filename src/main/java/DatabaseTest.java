import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/seahorse_chess";
        String user = "root";
        String password = "root";

        try {
            Connection connection =
                    DriverManager.getConnection(url, user, password);

            System.out.println("Database connected!");

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(
                    "SELECT DATABASE()"
            );

            if (result.next()) {
                System.out.println(
                        "Connected to: " + result.getString(1)
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

