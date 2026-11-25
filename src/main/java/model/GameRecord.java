
package model;

import java.sql.Timestamp;

public class GameRecord {

    private final int gameId;
    private final String status;
    private final Timestamp startedAt;
    private final Timestamp finishedAt;

    public GameRecord(int gameId, String status,
                      Timestamp startedAt,
                      Timestamp finishedAt) {

        this.gameId = gameId;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public int getGameId() {
        return gameId;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }
}
