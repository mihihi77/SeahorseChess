package database;

import dao.GameDAO;
import model.GameRecord;

import java.sql.SQLException;
import java.util.List;

public class GameDAOTest {

    public static void main(String[] args) throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<GameRecord> games = gameDAO.getAllGames();

        System.out.println("Total games: " + games.size());

        for (GameRecord game : games) {

            System.out.println("Game ID: " + game.getGameId());
            System.out.println("Status: " + game.getStatus());
            System.out.println("Started: " + game.getStartedAt());
            System.out.println("Finished: " + game.getFinishedAt());

            System.out.println("--------------------");
        }
    }
}
