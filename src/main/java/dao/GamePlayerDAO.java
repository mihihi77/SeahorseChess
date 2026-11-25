
package dao;

import model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GamePlayerDAO {

    void addGamePlayer(Connection conn, int gameId,
                       Player player) throws SQLException {

        String sql = "INSERT INTO GAME_PLAYER "
                   + "(game_id, player_id, turn_order, color_code) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            stmt.setInt(2, player.getPlayerId());
            stmt.setInt(3, player.getTurnOrder());
            stmt.setString(4, player.getColor().name());

            stmt.executeUpdate();
        }
    }

    void updateFinalRank(Connection conn, int gameId,
                         Player player) throws SQLException {

        String sql = "UPDATE GAME_PLAYER "
                   + "SET final_rank = ? "
                   + "WHERE game_id = ? AND player_id = ? "
                   + "AND final_rank IS NULL";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, player.getFinishingRank());
            stmt.setInt(2, gameId);
            stmt.setInt(3, player.getPlayerId());

            int rows = stmt.executeUpdate();

            if (rows != 1) {
                throw new SQLException(
                    "Cannot update rank for player "
                    + player.getPlayerId());
            }
        }
    }
}
