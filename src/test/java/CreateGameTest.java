
import dao.GameDAO;
import model.Player;

import java.sql.SQLException;
import java.util.List;

public class CreateGameTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<Player> players = TestData.createPlayers(2);

        int gameId = gameDAO.createGame(players);

        TestData.checkGame(
                gameId, "IN_PROGRESS", 2, 0, false);

        System.out.println("Create game: SUCCESS");
        System.out.println("Game ID: " + gameId);
    }
}
