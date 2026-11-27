package database;

import dao.PlayerDAO;
import model.Player;

import java.sql.SQLException;
import java.util.List;

public class PlayerDAOTest {

    public static void main(String[] args) throws SQLException {

        PlayerDAO playerDAO = new PlayerDAO();

        List<Player> players = playerDAO.getAllPlayers();

        System.out.println("Total players: " + players.size());

        for (Player player : players) {

            System.out.println("ID: " + player.getPlayerId());
            System.out.println("Username: " + player.getUsername());
            System.out.println("Name: " + player.getName());
            System.out.println("--------------------");
        }

        if (players.isEmpty()) {
            System.out.println("No players available for search tests.");
            return;
        }

        Player firstPlayer = players.get(0);

        Player foundPlayer =
                playerDAO.findById(firstPlayer.getPlayerId());

        if (foundPlayer == null ||
                foundPlayer.getPlayerId() != firstPlayer.getPlayerId()) {

            throw new IllegalStateException("Find by ID failed.");
        }

        System.out.println("Find by ID: SUCCESS");

        Player foundUsername =
                playerDAO.findByUsername(firstPlayer.getUsername());

        if (foundUsername == null ||
                foundUsername.getPlayerId() != firstPlayer.getPlayerId()) {

            throw new IllegalStateException("Find by username failed.");
        }

        System.out.println("Find by username: SUCCESS");
    }
}
