package just.fun.chess.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveConverterTest {

    @Test
    public void testForward() {
        //e2->e4
        SimpleMove move = new SimpleMove(12, 28);
        float[] array = new MoveConverter().doForward(move).getArray();
        assertArrayEquals(array, new float[]{0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0});
    }

    @Test
    public void testBackward() {
        //e2->e4
//        SimpleMove move = new SimpleMove(12, 28);
        SimpleMove move
                = new MoveConverter().doBackward(new FloatArrayHolder(new float[]{0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0}));
        assertEquals(move, new SimpleMove(12, 28));
    }

    @Test
    public void testBackwardFloat() {
        //e2->e4
//        SimpleMove move = new SimpleMove(12, 28);
        SimpleMove move
                = new MoveConverter().doBackward(new FloatArrayHolder(new float[]{0.3f, 0.4f, 0.8f, 0.6f, 0.49f, 0.44f, 0f, 1f, 0.5f, 1, 0, 0}));
        assertEquals(move, new SimpleMove(12, 28));
    }

}