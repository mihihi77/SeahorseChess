
package model;

import java.sql.Timestamp;

public class MatchResult {

    private final int gameId;
    private final int playerId;
    private final String username;
    private final String displayName;
    private final String colorCode;
    private final int finalRank;
    private final Timestamp finishedAt;

    public MatchResult(int gameId, int playerId,
                       String username, String displayName,
                       String colorCode, int finalRank,
                       Timestamp finishedAt) {

        this.gameId = gameId;
        this.playerId = playerId;
        this.username = username;
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.finalRank = finalRank;
        this.finishedAt = finishedAt;
    }

    public int getGameId() {
        return gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public int getFinalRank() {
        return finalRank;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }
}
