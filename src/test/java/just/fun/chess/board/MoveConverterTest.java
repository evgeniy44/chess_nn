package just.fun.chess.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveConverterTest {

    @Test
    public void testForward() {
        //a2->a3
        SimpleMove move = new SimpleMove(8, 16, 0);
        byte[] array = new MoveConverter().doForward(move).getArray();
        assertArrayEquals(array, new byte[]{0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0});
    }

    @Test
    public void testForwardBlack() {
        //a2->a3
        SimpleMove move = new SimpleMove(8, 16, 1);
        byte[] array = new MoveConverter().doForward(move).getArray();
        assertArrayEquals(array, new byte[]{1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1});
    }
}