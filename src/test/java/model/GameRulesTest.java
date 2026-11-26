package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {

    private GameRules rules;
    private Player green;
    private Player red;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        rules = new GameRules();
        green = new Player(1, "p1", PlayerColor.GREEN);
        red = new Player(2, "p2", PlayerColor.RED);
        players = List.of(green, red);
    }

    @Test
    void deployRequiresOneOrSix() {
        Piece g1 = green.getPieces().get(0);

        assertTrue(rules.isValidMove(g1, 1, players));
        assertTrue(rules.isValidMove(g1, 6, players));
        assertFalse(rules.isValidMove(g1, 2, players));
        assertFalse(rules.isValidMove(g1, 5, players));
    }

    @Test
    void cannotDeployWhenFriendlyPieceOccupiesStartSquare() {
        Piece g1 = green.getPieces().get(0);
        Piece g2 = green.getPieces().get(1);

        g1.deploy(rules.getStartSquare(PlayerColor.GREEN));

        assertFalse(rules.isValidMove(g2, 6, players));
    }

    @Test
    void capturesOpponentOnMainTrack() {
        Piece g1 = green.getPieces().get(0);
        Piece r1 = red.getPieces().get(0);

        r1.deploy(10);
        g1.deploy(0);
        g1.moveTo(6, 6);

        assertEquals(r1, rules.findCapturablePiece(g1, 10, players));
        assertTrue(rules.canCapture(g1, 10, players));
    }

    @Test
    void cannotOvershootHomeGate() {
        Piece g1 = green.getPieces().get(0);
        g1.deploy(0);
        g1.moveTo(37, 37); // 2 steps away from gate (step 39)

        assertTrue(rules.isValidMove(g1, 2, players));
        assertFalse(rules.isValidMove(g1, 3, players));
    }

    // Must roll a 6 to enter home column - might need to UPDATE later
    @Test
    void homeGateRequiresRollingSixToEnterHomeColumn() {
        Piece g1 = green.getPieces().get(0);
        g1.deploy(0);
        g1.moveTo(39, 39);

        assertTrue(rules.isAtHomeGate(g1));
        assertFalse(rules.isValidMove(g1, 1, players));
        assertFalse(rules.isValidMove(g1, 5, players));
        assertTrue(rules.isValidMove(g1, 6, players));
    }

    @Test
    void cannotOvershootFinalHomeSquare() {
        Piece g1 = green.getPieces().get(0);
        g1.deploy(0);
        g1.moveTo(39, 39);
        // Enter home column at index 0 (stepsTaken = 40), then move to index 4 (stepsTaken = 44)
        g1.moveTo(GameRules.HOME_POSITION_OFFSET, 1);
        g1.moveTo(GameRules.HOME_POSITION_OFFSET + 4, 4);

        assertTrue(rules.isValidMove(g1, 1, players));  // lands on index 5 (finish)
        assertFalse(rules.isValidMove(g1, 2, players)); // overshoots
    }
}