package model;

public class Piece extends GameEntity {
    public enum State {
        IN_BASE,
        ON_BOARD,
        FINISHED
    }

    public static final int BASE_POSITION = -1;

    private final int pieceNumber; // 1..4 per player
    private final PlayerColor color;
    private State state;
    private int position;
    private int stepsTaken;

    public Piece(int pieceNumber, PlayerColor color) {
        super();
        this.pieceNumber = pieceNumber;
        this.color = color;
        resetToBase();
    }

    public void deploy(int startSquare) {
        this.state = State.ON_BOARD;
        this.position = startSquare;
        this.stepsTaken = 0;
    }

    public void moveTo(int newPosition, int steps) {
        this.state = State.ON_BOARD;
        this.position = newPosition;
        this.stepsTaken += steps;
    }

    public void finish(int finishPosition) {
        this.state = State.FINISHED;
        this.position = finishPosition;
    }

    public void resetToBase() {
        this.state = State.IN_BASE;
        this.position = BASE_POSITION;
        this.stepsTaken = 0;
    }

    public boolean isInBase() { return state == State.IN_BASE; }
    public boolean isOnBoard() { return state == State.ON_BOARD; }
    public boolean isFinished() { return state == State.FINISHED; }

    public int getPieceId() { return getId(); }
    public void setPieceId(int pieceId) { setId(pieceId); }

    @Override
    public String getEntityType() {
        return "Piece";
    }

    public int getPieceNumber() { return pieceNumber; }
    public PlayerColor getColor() { return color; }
    public State getState() { return state; }
    public int getPosition() { return position; }
    public int getStepsTaken() { return stepsTaken; }
}