package just.fun.chess.board;

import chesspresso.Chess;
import chesspresso.position.Position;
import com.google.common.base.Converter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PositionConverter extends Converter<Position, FloatArrayHolder> {
    @Override
    protected FloatArrayHolder doForward(Position position) {
        if (position.getToPlay() == Chess.WHITE) {
            byte[] floats = new byte[64];
            int current = 0;
            for (int i = 0; i < 64; i++) {
                floats[current] = (byte) position.getStone(i);
                current++;
            }
            return new FloatArrayHolder(floats);
        } else {
            byte[] floats = new byte[64];
            int current = 0;
            for (int i = 63; i >= 0; i--) {
                floats[current] = (byte) ((-1) * position.getStone(i));
                current++;
            }
            return new FloatArrayHolder(floats);
        }
    }

    @Override
    protected Position doBackward(FloatArrayHolder floatArrayHolder) {
        throw new NotImplementedException();
    }
}
