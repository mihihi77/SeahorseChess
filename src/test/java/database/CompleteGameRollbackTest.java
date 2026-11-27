package database;

import dao.GameDAO;
import model.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompleteGameRollbackTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<Player> availablePlayers =
                TestData.createPlayers(3);

        Player player1 = availablePlayers.get(0);
        Player player2 = availablePlayers.get(1);
        Player outsider = availablePlayers.get(2);

        List<Player> players = new ArrayList<>();

        players.add(player1);
        players.add(player2);

        int gameId = gameDAO.createGame(players);

        player1.setFinishingRank(1);
        outsider.setFinishingRank(2);

        List<Player> results = new ArrayList<>();

        results.add(player1);
        results.add(outsider);

        try {

            gameDAO.completeGame(gameId, results);

            throw new IllegalStateException(
                    "Invalid result was accepted.");

        } catch (SQLException e) {

            if (!e.getMessage().contains(
                    "Cannot update rank for player "
                    + outsider.getPlayerId())) {

                throw e;
            }

            System.out.println("Expected error:");
            System.out.println(e.getMessage());
        }

        TestData.checkGame(
                gameId, "IN_PROGRESS", 2, 0, false);

        System.out.println("Complete game rollback: SUCCESS");
        System.out.println("Game ID: " + gameId);
    }
}
