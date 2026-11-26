package model;

import java.time.LocalDateTime;

public class Move extends GameEntity {
    private final int gameId;
    private final int moveNumber;
    private final int playerId;
    private final int diceValue;
    private final int pieceNumber;
    private final int fromPosition;
    private final int toPosition;
    private final boolean capturedOpponent;
    private final LocalDateTime createdAt;

    public Move(int gameId, int moveNumber, int playerId, int diceValue,
                int pieceNumber, int fromPosition, int toPosition, boolean capturedOpponent) {
        this(0, gameId, moveNumber, playerId, diceValue,
                pieceNumber, fromPosition, toPosition, capturedOpponent, LocalDateTime.now());
    }

    public Move(int moveId, int gameId, int moveNumber, int playerId, int diceValue,
                int pieceNumber, int fromPosition, int toPosition,
                boolean capturedOpponent, LocalDateTime createdAt) {
        super(moveId);
        this.gameId = gameId;
        this.moveNumber = moveNumber;
        this.playerId = playerId;
        this.diceValue = diceValue;
        this.pieceNumber = pieceNumber;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.capturedOpponent = capturedOpponent;
        this.createdAt = createdAt;
    }

    public int getMoveId() { return getId(); }
    public void setMoveId(int moveId) { setId(moveId); }
    public int getGameId() { return gameId; }
    public int getMoveNumber() { return moveNumber; }
    public int getPlayerId() { return playerId; }
    public int getDiceValue() { return diceValue; }
    public int getPieceNumber() { return pieceNumber; }
    public int getFromPosition() { return fromPosition; }
    public int getToPosition() { return toPosition; }
    public boolean isCapturedOpponent() { return capturedOpponent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String getEntityType() {
        return "Move";
    }
}