package database;

import dao.GameDAO;
import dao.MoveDAO;

import model.GameRules;
import model.Move;
import model.Piece;
import model.Player;

import java.sql.SQLException;
import java.util.List;

public class MoveDAOTest {

    public static void main(String[] args)
            throws SQLException {

        GameDAO gameDAO = new GameDAO();
        MoveDAO moveDAO = new MoveDAO();

        List<Player> players = TestData.createPlayers(2);

        int gameId = gameDAO.createGame(players);

        Player player = players.get(0);

        GameRules rules = new GameRules();

        int startPosition =
                rules.getStartSquare(player.getColor());

        // Test 1: Deploy a piece
        Move deployMove = new Move(
                gameId,
                1,
                player.getPlayerId(),
                6,
                1,
                Piece.BASE_POSITION,
                startPosition,
                false
        );

        int moveId = moveDAO.addMove(deployMove);

        System.out.println("Deploy move ID: " + moveId);

        // Test 2: Move the piece
        Move normalMove = new Move(
                gameId,
                2,
                player.getPlayerId(),
                3,
                1,
                startPosition,
                (startPosition + 3) % GameRules.TRACK_LENGTH,
                false
        );

        moveDAO.addMove(normalMove);

        // Test 3: Read saved moves
        List<Move> moves = moveDAO.getMovesByGame(gameId);

        if (moves.size() != 2
                || moves.get(0).getFromPosition() != Piece.BASE_POSITION
                || moves.get(0).getToPosition() != startPosition
                || moves.get(1).getFromPosition() != startPosition
                || moves.get(1).getToPosition()
                    != normalMove.getToPosition()) {

            throw new IllegalStateException(
                    "Incorrect saved moves.");
        }

        System.out.println("Game ID: " + gameId);
        System.out.println("Total moves: " + moves.size());

        for (Move move : moves) {

            System.out.println(
                    "Move " + move.getMoveNumber()
                    + ": " + move.getFromPosition()
                    + " -> " + move.getToPosition());
        }

        System.out.println("Move DAO test: SUCCESS");
    }
}
