package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameRules {

    public static final int TRACK_LENGTH = 40;
    public static final int HOME_LENGTH = 6;
    // Positions 0..39 are the main loop; 100..105 represent home column squares 1..6
    public static final int HOME_POSITION_OFFSET = 100;

    private static final Map<PlayerColor, Integer> START_SQUARES = Map.of(
            PlayerColor.GREEN, 0,
            PlayerColor.RED, 10,
            PlayerColor.BLUE, 20,
            PlayerColor.YELLOW, 30
    );

    public int getStartSquare(PlayerColor color) {
        return START_SQUARES.get(color);
    }

    public boolean canLeaveStable(int diceValue) {
        return diceValue == 1 || diceValue == 6;
    }

    public boolean isAtHomeGate(Piece piece) {
        return piece.isOnBoard() && piece.getStepsTaken() == TRACK_LENGTH - 1;
    }

    public boolean isInHomeColumn(Piece piece) {
        return piece.isOnBoard() && piece.getStepsTaken() >= TRACK_LENGTH;
    }

    public boolean isValidMove(Piece piece, int diceValue, List<Player> allPlayers) {
        if (piece == null || piece.isFinished() || diceValue < 1 || diceValue > 6) {
            return false;
        }

        // 1. Deploying from base (requires 1 or 6)
        if (piece.isInBase()) {
            if (!canLeaveStable(diceValue)) return false;
            int startSq = getStartSquare(piece.getColor());
            return !hasFriendlyPieceAt(piece.getColor(), startSq, allPlayers);
        }

        // 2. Waiting at the home gate (must roll 6 to enter home column)
        if (isAtHomeGate(piece)) {
            if (diceValue != 6) return false;
            int firstHomePos = HOME_POSITION_OFFSET;
            return !hasFriendlyPieceAt(piece.getColor(), firstHomePos, allPlayers);
        }

        // 3. Moving inside the home column (must not overshoot final square)
        if (isInHomeColumn(piece)) {
            int currentHomeIndex = piece.getStepsTaken() - TRACK_LENGTH; // 0..5
            int targetHomeIndex = currentHomeIndex + diceValue;
            if (targetHomeIndex >= HOME_LENGTH) return false;
            int targetPos = HOME_POSITION_OFFSET + targetHomeIndex;
            return !hasFriendlyPieceAt(piece.getColor(), targetPos, allPlayers);
        }

        // 4. Moving along the main loop (cannot overshoot the home gate)
        int targetSteps = piece.getStepsTaken() + diceValue;
        if (targetSteps > TRACK_LENGTH - 1) {
            return false;
        }
        int targetPos = (getStartSquare(piece.getColor()) + targetSteps) % TRACK_LENGTH;
        return !hasFriendlyPieceAt(piece.getColor(), targetPos, allPlayers);
    }

    public List<Piece> getValidPieces(Player player, int diceValue, List<Player> allPlayers) {
        List<Piece> valid = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            if (isValidMove(piece, diceValue, allPlayers)) {
                valid.add(piece);
            }
        }
        return valid;
    }

    /**
     * Returns the opponent piece that will be captured at targetPosition on the main loop, or null.
     */
    public Piece findCapturablePiece(Piece movingPiece, int targetPosition, List<Player> allPlayers) {
        if (targetPosition >= HOME_POSITION_OFFSET) {
            return null; // No captures inside home columns
        }
        for (Player player : allPlayers) {
            if (player.getColor() == movingPiece.getColor()) continue;
            for (Piece other : player.getPieces()) {
                if (other.isOnBoard() && other.getPosition() == targetPosition) {
                    return other;
                }
            }
        }
        return null;
    }

    public boolean canCapture(Piece movingPiece, int targetPosition, List<Player> allPlayers) {
        return findCapturablePiece(movingPiece, targetPosition, allPlayers) != null;
    }

    public boolean checkVictory(Player player) {
        return player != null && player.hasFinishedAllPieces();
    }

    private boolean hasFriendlyPieceAt(PlayerColor color, int position, List<Player> allPlayers) {
        for (Player player : allPlayers) {
            if (player.getColor() != color) continue;
            for (Piece p : player.getPieces()) {
                if (p.isOnBoard() && p.getPosition() == position) {
                    return true;
                }
            }
        }
        return false;
    }
}