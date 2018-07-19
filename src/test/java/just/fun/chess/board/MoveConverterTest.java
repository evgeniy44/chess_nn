package just.fun.chess.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveConverterTest {

    @Test
    public void testForward() {
        //e2->e4
        SimpleMove move = new SimpleMove(12, 28);
        byte[] array = new MoveConverter().doForward(move).getArray();
        assertArrayEquals(array, new byte[]{0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0});
    }

    @Test
    public void testBackward() {
        //e2->e4
//        SimpleMove move = new SimpleMove(12, 28);
        SimpleMove move
                = new MoveConverter().doBackward(new FloatArrayHolder(new byte[]{0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0}));
        assertEquals(move, new SimpleMove(12, 28));
    }
}