
import dao.GameDAO;
import model.Player;
import model.MatchResult;

import java.sql.SQLException;
import java.util.List;

public class CompleteGameTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<Player> players = TestData.createPlayers(2);

        int gameId = gameDAO.createGame(players);

        players.get(0).setFinishingRank(1);
        players.get(1).setFinishingRank(2);

        gameDAO.completeGame(gameId, players);

        TestData.checkGame(
                gameId, "COMPLETED", 2, 2, true);

        int correctRanks = 0;

        for (MatchResult result : gameDAO.getMatchHistory()) {

            if (result.getGameId() == gameId) {

                for (Player player : players) {

                    if (result.getPlayerId()
                            == player.getPlayerId()
                            && result.getFinalRank()
                            == player.getFinishingRank()) {

                        correctRanks++;
                    }
                }
            }
        }

        if (correctRanks != 2) {
            throw new IllegalStateException(
                    "Incorrect match results.");
        }

        System.out.println("Complete game: SUCCESS");
        System.out.println("Game ID: " + gameId);
    }
}
