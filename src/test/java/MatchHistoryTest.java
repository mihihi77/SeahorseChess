
import dao.GameDAO;
import model.MatchResult;

import java.sql.SQLException;
import java.util.List;

public class MatchHistoryTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();

        List<MatchResult> results =
                gameDAO.getMatchHistory();

        System.out.println(
                "Total result rows: " + results.size());

        for (MatchResult result : results) {

            System.out.println(
                    "Game ID: " + result.getGameId());

            System.out.println(
                    "Player: " + result.getDisplayName());

            System.out.println(
                    "Color: " + result.getColorCode());

            System.out.println(
                    "Rank: " + result.getFinalRank());

            System.out.println(
                    "Finished: " + result.getFinishedAt());

            System.out.println("--------------------");
        }
    }
}
