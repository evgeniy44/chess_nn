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
        int col = position % 8;
        int row = 7 - (position / 8);
        return (row * 8) + col;
    }

    @Override
    protected SimpleMove doBackward(FloatArrayHolder floatArrayHolder) {
        String binary = asString(floatArrayHolder);
        Integer from = Integer.parseInt(binary.substring(0, 6), 2);
        Integer to = Integer.parseInt(binary.substring(6, 12), 2);
        return new SimpleMove(from, to, 0);
    }

    private String asString(FloatArrayHolder floatArrayHolder) {
        StringBuilder builder = new StringBuilder();
        for (byte item : floatArrayHolder.getArray()) {
            builder.append(Math.round(item));
        }
        return builder.toString();
    }
}
