package just.fun.chess.board;

import chesspresso.Chess;
import com.google.common.base.Converter;

public class MoveConverter extends Converter<SimpleMove, FloatArrayHolder> {

    @Override
    protected FloatArrayHolder doForward(SimpleMove move) {
        int from = move.getFrom();
        int to = move.getTo();
        if (move.getPlayer() == Chess.BLACK) {
            from = flip(from);
            to = flip(to);
        }

        byte[] floats = new byte[12];
        String fromBinary = String.format("%6s", Integer.toBinaryString(from)).replace(' ', '0');
        String toBinary = String.format("%6s", Integer.toBinaryString(to)).replace(' ', '0');
        int curentSymbol = 0;
        for (int i = 0; i < fromBinary.length(); i++) {
            floats[curentSymbol] = (byte) (fromBinary.charAt(i) == '1' ? 1 : 0);
            curentSymbol++;
        }
        for (int i = 0; i < toBinary.length(); i++) {
            floats[curentSymbol] = (byte) (toBinary.charAt(i) == '1' ? 1 : 0);
            curentSymbol++;
        }
        return new FloatArrayHolder(floats);
    }

    private int flip(int position) {
        return 63 - position;
    }

    @Override
    protected SimpleMove doBackward(FloatArrayHolder floatArrayHolder) {
        throw new UnsupportedOperationException();
    }
}
