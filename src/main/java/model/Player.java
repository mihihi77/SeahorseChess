package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    public static final int PIECES_PER_PLAYER = 4;

    private int playerId;
    private String username;
    private String name;
    private PlayerColor color;
    private int turnOrder;
    private int finishingRank;
    private final List<Piece> pieces = new ArrayList<>();

    public Player() {
    }

    public Player(int turnOrder, String name, PlayerColor color) {
        this.turnOrder = turnOrder;
        this.name = name;
        this.username = name.toLowerCase();
        this.color = color;
        this.finishingRank = 0;
        initPieces();
    }

    public void initPieces() {
        pieces.clear();
        if (color == null) return;
        for (int i = 1; i <= PIECES_PER_PLAYER; i++) {
            pieces.add(new Piece(i, color));
        }
    }

    public boolean hasFinishedAllPieces() {
        if (pieces.isEmpty()) return false;
        for (Piece p : pieces) {
            if (!p.isFinished()) return false;
        }
        return true;
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PlayerColor getColor() { return color; }
    public void setColor(PlayerColor color) {
        this.color = color;
        if (pieces.isEmpty()) initPieces();
    }

    public int getTurnOrder() { return turnOrder; }
    public void setTurnOrder(int turnOrder) { this.turnOrder = turnOrder; }

    public int getFinishingRank() { return finishingRank; }
    public void setFinishingRank(int finishingRank) { this.finishingRank = finishingRank; }

    public List<Piece> getPieces() { return Collections.unmodifiableList(pieces); }
}