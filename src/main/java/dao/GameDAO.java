
package dao;

import model.GameRecord;
import model.Player;
import model.MatchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    private final GamePlayerDAO gamePlayerDAO = new GamePlayerDAO();

    public List<GameRecord> getAllGames()
            throws SQLException {

        List<GameRecord> games = new ArrayList<>();

        String sql = "SELECT game_id, status, "
                + "started_at, finished_at "
                + "FROM GAME ORDER BY game_id";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                GameRecord game = new GameRecord(
                        rs.getInt("game_id"),
                        rs.getString("status"),
                        rs.getTimestamp("started_at"),
                        rs.getTimestamp("finished_at"));

                games.add(game);
            }
        }

        return games;
    }

    public int createGame(List<Player> players)
            throws SQLException {

        validatePlayers(players);

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                int gameId = insertGame(conn);

                for (Player player : players) {

                    gamePlayerDAO.addGamePlayer(
                            conn, gameId, player);
                }

                conn.commit();

                return gameId;

            } catch (SQLException | RuntimeException e) {

                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    private int insertGame(Connection conn)
            throws SQLException {

        String sql = "INSERT INTO GAME (status) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(
                sql, new String[] { "GAME_ID" })) {

            stmt.setString(1, "IN_PROGRESS");

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new SQLException(
                        "Cannot get generated game ID.");
            }
        }
    }

    private void validatePlayers(List<Player> players) {

        if (players == null ||
                players.size() < 2 ||
                players.size() > 4) {

            throw new IllegalArgumentException(
                    "A game requires 2 to 4 players.");
        }

        for (int i = 0; i < players.size(); i++) {

            Player player = players.get(i);

            if (player == null ||
                    player.getPlayerId() <= 0 ||
                    player.getColor() == null) {

                throw new IllegalArgumentException(
                        "Invalid player information.");
            }

            if (player.getTurnOrder() != i + 1) {

                throw new IllegalArgumentException(
                        "Invalid turn order.");
            }
        }
    }

    public void completeGame(int gameId, List<Player> players)
            throws SQLException {

        validateFinalRanks(players);

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                updateGameStatus(conn, gameId, "COMPLETED");

                if (countGamePlayers(conn, gameId) != players.size()) {

                    throw new SQLException(
                            "Incorrect number of players.");
                }

                for (Player player : players) {

                    gamePlayerDAO.updateFinalRank(
                            conn, gameId, player);
                }

                conn.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    private int countGamePlayers(Connection conn, int gameId)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM GAME_PLAYER "
                + "WHERE game_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {

                rs.next();

                return rs.getInt(1);
            }
        }
    }

    private void updateGameStatus(Connection conn, int gameId,
            String status)
            throws SQLException {

        String sql = "UPDATE GAME "
                + "SET status = ?, finished_at = SYSTIMESTAMP "
                + "WHERE game_id = ? AND status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, gameId);
            stmt.setString(3, "IN_PROGRESS");

            int rows = stmt.executeUpdate();

            if (rows != 1) {
                throw new SQLException(
                        "Game does not exist or is not in progress.");
            }
        }
    }

    private void validateFinalRanks(List<Player> players) {

        if (players == null ||
                players.size() < 2 ||
                players.size() > 4) {

            throw new IllegalArgumentException(
                    "A game requires 2 to 4 players.");
        }

        boolean[] usedRanks = new boolean[players.size() + 1];

        for (Player player : players) {

            if (player == null || player.getPlayerId() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid player.");
            }

            int rank = player.getFinishingRank();

            if (rank < 1 || rank > players.size()) {

                throw new IllegalArgumentException(
                        "Invalid finishing rank.");
            }

            if (usedRanks[rank]) {

                throw new IllegalArgumentException(
                        "Duplicate finishing rank.");
            }

            usedRanks[rank] = true;
        }
    }

    public void abandonGame(int gameId) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection()) {

            updateGameStatus(conn, gameId, "ABANDONED");
        }
    }

    public List<MatchResult> getMatchHistory()
            throws SQLException {

        List<MatchResult> results = new ArrayList<>();

        String sql = "SELECT g.game_id, p.player_id, "
                + "p.username, p.display_name, "
                + "gp.color_code, gp.final_rank, "
                + "g.finished_at "
                + "FROM GAME g "
                + "JOIN GAME_PLAYER gp "
                + "ON g.game_id = gp.game_id "
                + "JOIN PLAYER p "
                + "ON gp.player_id = p.player_id "
                + "WHERE g.status = 'COMPLETED' "
                + "ORDER BY g.finished_at DESC, "
                + "g.game_id DESC, gp.final_rank ASC";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                MatchResult result = new MatchResult(
                        rs.getInt("game_id"),
                        rs.getInt("player_id"),
                        rs.getString("username"),
                        rs.getString("display_name"),
                        rs.getString("color_code"),
                        rs.getInt("final_rank"),
                        rs.getTimestamp("finished_at"));

                results.add(result);
            }
        }

        return results;
    }

}
