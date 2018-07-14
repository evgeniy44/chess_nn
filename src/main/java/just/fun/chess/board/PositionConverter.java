package just.fun.chess.board;

import chesspresso.position.Position;
import com.google.common.base.Converter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PositionConverter extends Converter<Position, FloatArrayHolder> {
    @Override
    protected FloatArrayHolder doForward(Position position) {
        float[] floats = new float[65];
        int current = 0;
        floats[current] = position.getToPlay();
        current++;
        for (int i = 0; i < 64; i++) {
            floats[current] = position.getStone(i);
            current++;
        }
        return new FloatArrayHolder(floats);
    }

    @Override
    protected Position doBackward(FloatArrayHolder floatArrayHolder) {
        throw new NotImplementedException();
    }
}
