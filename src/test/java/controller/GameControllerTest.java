package controller;

import model.Game;
import model.Piece;
import model.Player;
import model.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private List<Player> twoPlayers;
    private GameController controller;

    @BeforeEach
    void setUp() {
        twoPlayers = List.of(
                new Player(1, "p1", PlayerColor.GREEN),
                new Player(2, "p2", PlayerColor.RED)
        );
        controller = new GameController(twoPlayers, null);
    }

    @Test
    void startGameSetsInProgressAndWaitsForRoll() {
        controller.startGame();

        // Check game status = IN_PROGRESS
        assertEquals(Game.Status.IN_PROGRESS, controller.getGame().getStatus());
        // Check turn phase status
        assertEquals(GameController.TurnPhase.WAITING_FOR_ROLL, controller.getPhase());
        // Correctly get current player = player 1's turn
        assertEquals(twoPlayers.get(0), controller.getGame().getCurrentPlayer());
    }

    @Test
    void rollDiceReturnsValidValueAndUpdatesPhase() {
        controller.startGame();

        int rolled = controller.rollDice();

        assertTrue(rolled >= 1 && rolled <= 6);
        // Allows deploying from base when rolling a 1 or 6
        // Turn phase = WAITING_FOR_MOVE
        if (rolled == 1 || rolled == 6) {
            assertEquals(GameController.TurnPhase.WAITING_FOR_MOVE, controller.getPhase());
        } else {
            // All pieces start in base, so 2..5 has no valid moves and passes turn
            assertEquals(GameController.TurnPhase.WAITING_FOR_ROLL, controller.getPhase());
            assertEquals(twoPlayers.get(1), controller.getGame().getCurrentPlayer());
        }
    }

    @Test
    void movePieceRejectedBeforeRolling() {
        controller.startGame();
        Piece g1 = twoPlayers.get(0).getPieces().get(0);

        assertFalse(controller.movePiece(g1));
    }

    @Test
    void exitGameTransitionsToGameOver() {
        controller.startGame();
        controller.exitGame();

        assertEquals(GameController.TurnPhase.GAME_OVER, controller.getPhase());
        assertEquals(-1, controller.rollDice());
    }

    // Simulates real 4Ps match
    @Test
    void fullFourPlayerMatchCompletesWithUniqueRanks() {
        List<Player> fourPlayers = List.of(
                new Player(1, "green", PlayerColor.GREEN),
                new Player(2, "red", PlayerColor.RED),
                new Player(3, "blue", PlayerColor.BLUE),
                new Player(4, "yellow", PlayerColor.YELLOW)
        );

        GameController matchController = new GameController(fourPlayers, null);
        matchController.startGame();

        int maxRolls = 50_000;
        int rolls = 0;

        while (matchController.getPhase() != GameController.TurnPhase.GAME_OVER && rolls < maxRolls) {
            int rolled = matchController.rollDice();
            assertTrue(rolled >= 1 && rolled <= 6);
            rolls++;

            if (matchController.getPhase() == GameController.TurnPhase.WAITING_FOR_MOVE) {
                Player current = matchController.getGame().getCurrentPlayer();
                List<Piece> valid = matchController.getGame().getRules()
                        .getValidPieces(current, rolled, matchController.getGame().getPlayers());
                assertFalse(valid.isEmpty());
                assertTrue(matchController.movePiece(valid.get(0)));
            }
        }

        assertEquals(Game.Status.FINISHED, matchController.getGame().getStatus());
        assertEquals(GameController.TurnPhase.GAME_OVER, matchController.getPhase());

        Set<Integer> ranks = new HashSet<>();
        for (Player p : matchController.getGame().getPlayers()) {
            assertTrue(p.getFinishingRank() >= 1 && p.getFinishingRank() <= 4);
            ranks.add(p.getFinishingRank());
        }
        assertEquals(4, ranks.size(), "All 4 players should have distinct finishing ranks");
    }
}