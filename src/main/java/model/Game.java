package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED
    }

    private int gameId;
    private final List<Player> players;
    private final Die die;
    private final GameRules rules;
    private final List<Move> moveHistory = new ArrayList<>();

    private int currentPlayerIndex;
    private int turnNumber;
    private int diceValue;
    private int nextFinishingRank;
    private Status status;

    public Game(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.die = new Die();
        this.rules = new GameRules();
        this.currentPlayerIndex = 0;
        this.turnNumber = 0;
        this.diceValue = 0;
        this.nextFinishingRank = 1;
        this.status = Status.NOT_STARTED;
    }

    public void start() {
        this.status = Status.IN_PROGRESS;
        this.currentPlayerIndex = 0;
        this.turnNumber = 1;
        this.nextFinishingRank = 1;
    }

    public int rollDie() {
        this.diceValue = die.roll();
        return diceValue;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Advances to the next player who has not yet finished.
     */
    public void nextTurn() {
        if (status != Status.IN_PROGRESS) return;

        int total = players.size();
        for (int i = 1; i <= total; i++) {
            int candidate = (currentPlayerIndex + i) % total;
            if (players.get(candidate).getFinishingRank() == 0) {
                currentPlayerIndex = candidate;
                turnNumber++;
                diceValue = 0;
                return;
            }
        }
    }

    /**
     * Assigns a finishing rank if the current player just finished all 4 pieces.
     * Ends the game when 1 or fewer unfinished players remain.
     */
    public Player checkWinner() {
        Player current = getCurrentPlayer();
        if (current.getFinishingRank() == 0 && rules.checkVictory(current)) {
            current.setFinishingRank(nextFinishingRank++);
            assignLastPlaceIfGameOver();
            return current;
        }
        return null;
    }

    private void assignLastPlaceIfGameOver() {
        List<Player> remaining = new ArrayList<>();
        for (Player p : players) {
            if (p.getFinishingRank() == 0) {
                remaining.add(p);
            }
        }
        if (remaining.size() <= 1) {
            if (remaining.size() == 1) {
                remaining.get(0).setFinishingRank(nextFinishingRank++);
            }
            this.status = Status.FINISHED;
        }
    }

    public void recordMove(Move move) {
        moveHistory.add(move);
    }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public Die getDie() { return die; }
    public GameRules getRules() { return rules; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public int getTurnNumber() { return turnNumber; }
    public int getDiceValue() { return diceValue; }
    public Status getStatus() { return status; }
    public List<Move> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }
}
