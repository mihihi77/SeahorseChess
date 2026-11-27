package database;

import dao.GameDAO;
import model.Player;
import model.PlayerColor;

import java.sql.SQLException;
import java.util.List;

public class RollbackTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<Player> players = TestData.createPlayers(2);

        players.get(0).setColor(PlayerColor.BLUE);
        players.get(1).setColor(PlayerColor.BLUE);

        players.get(0).initPieces();
        players.get(1).initPieces();

        int gamesBefore = gameDAO.getAllGames().size();

        boolean expectedError = false;

        try {

            gameDAO.createGame(players);

        } catch (SQLException e) {

            System.out.println("Expected error:");
            System.out.println(e.getMessage());

            expectedError = e.getErrorCode() == 1
                    && e.getMessage().contains("UQ_GP_COLOR");

            if (!expectedError) {
                throw e;
            }
        }

        int gamesAfter = gameDAO.getAllGames().size();

        System.out.println("Games before: " + gamesBefore);
        System.out.println("Games after: " + gamesAfter);

        if (!expectedError || gamesBefore != gamesAfter) {

            throw new IllegalStateException(
                    "Rollback test: FAILED");
        }

        System.out.println("Rollback test: SUCCESS");
    }
}
