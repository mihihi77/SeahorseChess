package dao;

import model.Move;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {

    public int addMove(Move move) throws SQLException {
        validateMove(move);

        String sql = "INSERT INTO GAME_MOVE "
                + "(game_id, move_number, player_id, dice_value, "
                + "piece_number, from_position, to_position, captured_opponent) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"MOVE_ID"})
        ) {
            stmt.setInt(1, move.getGameId());
            stmt.setInt(2, move.getMoveNumber());
            stmt.setInt(3, move.getPlayerId());
            stmt.setInt(4, move.getDiceValue());
            stmt.setInt(5, move.getPieceNumber());
            stmt.setInt(6, move.getFromPosition());
            stmt.setInt(7, move.getToPosition());
            stmt.setInt(8, move.isCapturedOpponent() ? 1 : 0);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    move.setMoveId(generatedId);
                    return generatedId;
                }
                throw new SQLException("Cannot get generated move ID.");
            }
        }
    }

    public List<Move> getMovesByGame(int gameId) throws SQLException {
        List<Move> moves = new ArrayList<>();

        String sql = "SELECT move_id, game_id, move_number, player_id, dice_value, "
                + "piece_number, from_position, to_position, captured_opponent, created_at "
                + "FROM GAME_MOVE WHERE game_id = ? ORDER BY move_number ASC";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, gameId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                    moves.add(new Move(
                            rs.getInt("move_id"),
                            rs.getInt("game_id"),
                            rs.getInt("move_number"),
                            rs.getInt("player_id"),
                            rs.getInt("dice_value"),
                            rs.getInt("piece_number"),
                            rs.getInt("from_position"),
                            rs.getInt("to_position"),
                            rs.getInt("captured_opponent") == 1,
                            createdAt
                    ));
                }
            }
        }
        return moves;
    }

    private void validateMove(Move move) {
        if (move == null || move.getGameId() <= 0 || move.getPlayerId() <= 0) {
            throw new IllegalArgumentException("Move must have valid gameId and playerId.");
        }
        if (move.getMoveNumber() <= 0) {
            throw new IllegalArgumentException("Move number must be positive.");
        }
        if (move.getDiceValue() < 1 || move.getDiceValue() > 6) {
            throw new IllegalArgumentException("Dice value must be between 1 and 6.");
        }
        if (move.getPieceNumber() < 1 || move.getPieceNumber() > 4) {
            throw new IllegalArgumentException("Piece number must be between 1 and 4.");
        }
    }
}