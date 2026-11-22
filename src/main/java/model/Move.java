package model;

import java.time.LocalDateTime;

public class Move {
    private int moveId;
    private int gameId;
    private int moveNumber;
    private int playerId;
    private int diceValue;
    private int pieceNumber;
    private int fromPosition;
    private int toPosition;
    private boolean capturedOpponent;
    private LocalDateTime createdAt;

    public Move(int gameId, int moveNumber, int playerId, int diceValue,
                int pieceNumber, int fromPosition, int toPosition, boolean capturedOpponent) {
        this.gameId = gameId;
        this.moveNumber = moveNumber;
        this.playerId = playerId;
        this.diceValue = diceValue;
        this.pieceNumber = pieceNumber;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.capturedOpponent = capturedOpponent;
        this.createdAt = LocalDateTime.now();
    }

    public int getMoveId() { return moveId; }
    public void setMoveId(int moveId) { this.moveId = moveId; }
    public int getGameId() { return gameId; }
    public int getMoveNumber() { return moveNumber; }
    public int getPlayerId() { return playerId; }
    public int getDiceValue() { return diceValue; }
    public int getPieceNumber() { return pieceNumber; }
    public int getFromPosition() { return fromPosition; }
    public int getToPosition() { return toPosition; }
    public boolean isCapturedOpponent() { return capturedOpponent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}