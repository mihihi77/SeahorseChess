package controller;

import model.Game;
import model.GameRules;
import model.Move;
import model.Piece;
import model.Player;
import view.GameView;

import java.util.*;
import java.util.stream.Collectors;

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
        if (this.view != null) {
            this.view.setController(this);
        }
    }

    public void startGame() {
        game.start();
        phase = TurnPhase.WAITING_FOR_ROLL;
        if (view != null) {
            view.displayWindow();
            view.updateBoard(game.getPlayers());
            view.showTurn(game.getCurrentPlayer());
            view.showMessage(game.getCurrentPlayer().getName() + "'s turn - roll the die!");
        }
    }

    public int rollDice() {
        if (phase != TurnPhase.WAITING_FOR_ROLL || game.getStatus() != Game.Status.IN_PROGRESS) {
            return -1;
        }

        int rolled = game.rollDie();
        Player current = game.getCurrentPlayer();
        List<Piece> validPieces = game.getRules().getValidPieces(current, rolled, game.getPlayers());

        if (view != null) {
            view.showDice(rolled);
        }

        if (validPieces.isEmpty()) {
            // No legal move available; pass turn (even on a 6, or keep turn if desired)
            game.nextTurn();
            phase = TurnPhase.WAITING_FOR_ROLL;
            if (view != null) {
                view.highlightValidPieces(Collections.emptyList());
                view.showTurn(game.getCurrentPlayer());
                view.showMessage(current.getName() + " rolled" + rolled + " (no valid moves).");
            }
        } else {
            phase = TurnPhase.WAITING_FOR_MOVE;
            if (view != null) {
                view.highlightValidPieces(validPieces);
                view.showMessage(current.getName() + " rolled" + rolled + " - select a piece.");
            }
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
            if (view != null) {
                List<Player> ranked = game.getPlayers().stream()
                        .sorted(Comparator.comparingInt(Player::getFinishingRank))
                        .collect(Collectors.toList());
                view.showGameOver(ranked);
            }
            return true;
        }

        // Extra turn on 6 (unless the player just finished the game)
        if (finishedPlayer == null && game.getDie().grantsExtraTurn()) {
            phase = TurnPhase.WAITING_FOR_ROLL;
            if (view != null) {
                view.showTurn(game.getCurrentPlayer());
                view.showMessage(current.getName() + " rolled a 6! - roll again!");
            }
        } else {
            game.nextTurn();
            phase = TurnPhase.WAITING_FOR_ROLL;
            if (view != null) {
                view.showTurn(game.getCurrentPlayer());
                view.showMessage(game.getCurrentPlayer().getName() + "'s turn - roll the die!");
            }
        }

        return true;
    }

    public void exitGame() {
        phase = TurnPhase.GAME_OVER;
        if (view != null) {
            view.closeWindow();
        }
    }

    public Game getGame() { return game; }
    public TurnPhase getPhase() { return phase; }
}
