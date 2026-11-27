package database;

import dao.DatabaseConnection;
import dao.PlayerDAO;
import model.Player;
import model.PlayerColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class TestData {

    static List<Player> createPlayers(int count)
            throws SQLException {

        PlayerDAO playerDAO = new PlayerDAO();

        List<Player> players = new ArrayList<>();

        String prefix = "test_" + UUID.randomUUID();

        PlayerColor[] colors = PlayerColor.values();

        for (int i = 0; i < count; i++) {

            String username = prefix + "_" + (i + 1);

            int inserted = playerDAO.addPlayer(
                    username, "Test Player " + (i + 1));

            if (inserted != 1) {
                throw new IllegalStateException(
                        "Cannot create test player.");
            }

            Player player = playerDAO.findByUsername(username);

            if (player == null) {
                throw new IllegalStateException(
                        "Test player not found.");
            }

            player.setTurnOrder(i + 1);
            player.setColor(colors[i]);

            players.add(player);
        }

        return players;
    }

    static void checkGame(int gameId, String status,
                          int playerCount, int rankedCount,
                          boolean finished)
            throws SQLException {

        String sql = "SELECT g.status, g.finished_at, "
                   + "COUNT(gp.player_id) AS player_count, "
                   + "COUNT(gp.final_rank) AS ranked_count "
                   + "FROM GAME g "
                   + "LEFT JOIN GAME_PLAYER gp "
                   + "ON g.game_id = gp.game_id "
                   + "WHERE g.game_id = ? "
                   + "GROUP BY g.status, g.finished_at";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new IllegalStateException(
                            "Game not found: " + gameId);
                }

                if (!status.equals(rs.getString("status"))
                        || rs.getInt("player_count") != playerCount
                        || rs.getInt("ranked_count") != rankedCount
                        || (rs.getTimestamp("finished_at") != null)
                            != finished) {

                    throw new IllegalStateException(
                            "Incorrect game state: " + gameId);
                }
            }
        }
    }
}
