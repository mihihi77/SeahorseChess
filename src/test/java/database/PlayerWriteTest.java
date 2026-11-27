package database;

import dao.PlayerDAO;
import model.Player;

import java.sql.SQLException;

public class PlayerWriteTest {

    public static void main(String[] args)
            throws SQLException {

        PlayerDAO playerDAO = new PlayerDAO();

        String username = "test_" + System.currentTimeMillis();

        // 1. Create player
        int inserted = playerDAO.addPlayer(
                username, "Test Player");

        if (inserted != 1) {
            throw new IllegalStateException("Insert failed.");
        }

        System.out.println("Add player: SUCCESS");

        // 2. Find by username
        Player player = playerDAO.findByUsername(username);

        if (player == null) {
            throw new IllegalStateException("Player not found.");
        }

        System.out.println("Find by username: SUCCESS");

        int playerId = player.getPlayerId();

        System.out.println("Player ID: " + playerId);
        System.out.println("Username: " + username);

        // 3. Update player
        int updated = playerDAO.updatePlayer(
                playerId, "Updated Test Player");

        if (updated != 1) {
            throw new IllegalStateException("Update failed.");
        }

        // 4. Verify updated information
        Player updatedPlayer = playerDAO.findById(playerId);

        if (updatedPlayer == null ||
                !"Updated Test Player".equals(
                        updatedPlayer.getName())) {

            throw new IllegalStateException(
                    "Updated information is incorrect.");
        }

        System.out.println("Find by ID: SUCCESS");
        System.out.println("Update player: SUCCESS");

        // 5. Test duplicate username
        try {

            playerDAO.addPlayer(username, "Duplicate Player");

            throw new IllegalStateException(
                    "Duplicate username was accepted.");

        } catch (SQLException e) {

            if (e.getErrorCode() != 1 ||
                    !e.getMessage().contains("UQ_PLAYER_USERNAME")) {
                throw e;
            }

            System.out.println("Duplicate username: REJECTED");
        }

        System.out.println("Player write test: SUCCESS");
    }
}
