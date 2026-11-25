
import dao.GameDAO;
import model.Player;

import java.sql.SQLException;
import java.util.List;

public class AbandonGameTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<Player> players = TestData.createPlayers(2);

        int gameId = gameDAO.createGame(players);

        gameDAO.abandonGame(gameId);

        TestData.checkGame(
                gameId, "ABANDONED", 2, 0, true);

        System.out.println("Abandon game: SUCCESS");
        System.out.println("Game ID: " + gameId);
    }
}
