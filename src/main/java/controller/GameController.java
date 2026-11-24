package controller;

import model.Game;
import model.GameRules;
import model.Move;
import model.Piece;
import model.Player;
import view.GameView;

import java.util.List;

public class GameController {

    public enum TurnPhase {
        WAITING_FOR_ROLL,
        WAITING_FOR_MOVE,
        GAME_OVER
    }

    private final Game game;
    private final GameView view;
    private TurnPhase phase;

    public GameController(List<Player> players, GameView view) {
        this.game = new Game(players);
        this.view = view;
        this.phase = TurnPhase.WAITING_FOR_ROLL;
    }

    public void startGame() {
        game.start();
        phase = TurnPhase.WAITING_FOR_ROLL;
        if (view != null) {
            view.displayWindow();
        }
    }

    public int rollDice() {
        if (phase != TurnPhase.WAITING_FOR_ROLL || game.getStatus() != Game.Status.IN_PROGRESS) {
            return -1;
        }

        int rolled = game.rollDie();
        Player current = game.getCurrentPlayer();
        List<Piece> validPieces = game.getRules().getValidPieces(current, rolled, game.getPlayers());

        if (validPieces.isEmpty()) {
            // No legal move available; pass turn (even on a 6, or keep turn if desired)
            game.nextTurn();
            phase = TurnPhase.WAITING_FOR_ROLL;
        } else {
            phase = TurnPhase.WAITING_FOR_MOVE;
        }

        return rolled;
    }

    public boolean movePiece(Piece piece) {
        if (phase != TurnPhase.WAITING_FOR_MOVE || game.getStatus() != Game.Status.IN_PROGRESS) {
            return false;
        }

        Player current = game.getCurrentPlayer();
        int dice = game.getDiceValue();
        GameRules rules = game.getRules();

        if (piece.getColor() != current.getColor() || !rules.isValidMove(piece, dice, game.getPlayers())) {
            return false;
        }

        int fromPos = piece.getPosition();
        int toPos;

        // Execute movement
        if (piece.isInBase()) {
            toPos = rules.getStartSquare(piece.getColor());
            piece.deploy(toPos);
        } else if (rules.isAtHomeGate(piece)) {
            toPos = GameRules.HOME_POSITION_OFFSET;
            piece.moveTo(toPos, 1); // Enters home column at step 0
        } else if (rules.isInHomeColumn(piece)) {
            int nextHomeIndex = (piece.getStepsTaken() - GameRules.TRACK_LENGTH) + dice;
            toPos = GameRules.HOME_POSITION_OFFSET + nextHomeIndex;
            if (nextHomeIndex == GameRules.HOME_LENGTH - 1) {
                piece.finish(toPos);
            } else {
                piece.moveTo(toPos, dice);
            }
        } else {
            toPos = (rules.getStartSquare(piece.getColor()) + piece.getStepsTaken() + dice) % GameRules.TRACK_LENGTH;
            piece.moveTo(toPos, dice);
        }

        // Check capture
        Piece captured = rules.findCapturablePiece(piece, toPos, game.getPlayers());
        boolean didCapture = captured != null;
        if (didCapture) {
            captured.resetToBase();
        }

        // Record move
        Move move = new Move(
                game.getGameId(),
                game.getMoveHistory().size() + 1,
                current.getPlayerId(),
                dice,
                piece.getPieceNumber(),
                fromPos,
                toPos,
                didCapture
        );
        game.recordMove(move);

        // Check if player finished all 4 pieces
        Player finishedPlayer = game.checkWinner();
        if (game.getStatus() == Game.Status.FINISHED) {
            phase = TurnPhase.GAME_OVER;
            return true;
        }

        // Extra turn on 6 (unless the player just finished the game)
        if (finishedPlayer == null && game.getDie().grantsExtraTurn()) {
            phase = TurnPhase.WAITING_FOR_ROLL;
        } else {
            game.nextTurn();
            phase = TurnPhase.WAITING_FOR_ROLL;
        }

        return true;
    }

    public void exitGame() {
        phase = TurnPhase.GAME_OVER;
        // Hook for closing GameView and returning to MenuView
    }

    public Game getGame() { return game; }
    public TurnPhase getPhase() { return phase; }
}
