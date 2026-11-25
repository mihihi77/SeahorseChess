
package dao;

import model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    public List<Player> getAllPlayers() throws SQLException {

        List<Player> players = new ArrayList<>();

        String sql = "SELECT player_id, username, display_name "
                   + "FROM PLAYER ORDER BY player_id";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                players.add(mapPlayer(rs));
            }
        }

        return players;
    }

    public int addPlayer(String username, String displayName)
            throws SQLException {

        String sql = "INSERT INTO PLAYER (username, display_name) "
                   + "VALUES (?, ?)";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);
            stmt.setString(2, displayName);

            return stmt.executeUpdate();
        }
    }

    public Player findByUsername(String username)
            throws SQLException {

        String sql = "SELECT player_id, username, display_name "
                   + "FROM PLAYER WHERE username = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapPlayer(rs);
                }
            }
        }

        return null;
    }

    public Player findById(int playerId) throws SQLException {

        String sql = "SELECT player_id, username, display_name "
                   + "FROM PLAYER WHERE player_id = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return mapPlayer(rs);
                }
            }
        }

        return null;
    }

    public int updatePlayer(int playerId, String newDisplayName)
            throws SQLException {

        String sql = "UPDATE PLAYER "
                   + "SET display_name = ? "
                   + "WHERE player_id = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, newDisplayName);
            stmt.setInt(2, playerId);

            return stmt.executeUpdate();
        }
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {

        Player player = new Player();

        player.setPlayerId(rs.getInt("player_id"));
        player.setUsername(rs.getString("username"));
        player.setName(rs.getString("display_name"));

        return player;
    }
}
