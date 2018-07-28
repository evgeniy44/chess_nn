package just.fun.chess.board;

import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import org.junit.jupiter.api.Test;

import static chesspresso.Chess.BLACK_BISHOP;
import static chesspresso.Chess.BLACK_KING;
import static chesspresso.Chess.BLACK_KNIGHT;
import static chesspresso.Chess.BLACK_PAWN;
import static chesspresso.Chess.BLACK_QUEEN;
import static chesspresso.Chess.BLACK_ROOK;
import static chesspresso.Chess.NO_STONE;
import static chesspresso.Chess.WHITE_BISHOP;
import static chesspresso.Chess.WHITE_KING;
import static chesspresso.Chess.WHITE_KNIGHT;
import static chesspresso.Chess.WHITE_PAWN;
import static chesspresso.Chess.WHITE_QUEEN;
import static chesspresso.Chess.WHITE_ROOK;
import static org.junit.jupiter.api.Assertions.*;

class PositionConverterTest {

    @Test
    public void testForward() {
        FloatArrayHolder holder = new PositionConverter().convert(new Game().getPosition());
        byte expectedPositionArray[] = new byte[] {
                WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK,
                WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
                BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
        };
        assertArrayEquals(holder.getArray(), expectedPositionArray);
    }

    @Test
    public void testForwardBlack() throws IllegalMoveException {
        Game game = new Game();
        short regularMove = Move.getRegularMove(8, 16, false); //e2 e4
        game.getPosition().doMove(regularMove);
        FloatArrayHolder holder = new PositionConverter().convert(game.getPosition());
        byte expectedPositionArray[] = new byte[] {
                WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK,
                WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE,
                NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, NO_STONE, BLACK_PAWN,
                BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, NO_STONE,
                BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
        };
        assertArrayEquals(holder.getArray(), expectedPositionArray);
    }

}