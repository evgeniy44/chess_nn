package just.fun.chess.board;

import com.google.common.base.Converter;

public class MoveConverter extends Converter<SimpleMove, FloatArrayHolder> {

    @Override
    protected FloatArrayHolder doForward(SimpleMove move) {
        byte[] floats = new byte[12];
        String fromBinary = String.format("%6s", Integer.toBinaryString(move.getFrom())).replace(' ', '0');
        String toBinary = String.format("%6s", Integer.toBinaryString(move.getTo())).replace(' ', '0');
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

    @Override
    protected SimpleMove doBackward(FloatArrayHolder floatArrayHolder) {
         String binary = asString(floatArrayHolder);
        Integer from = Integer.parseInt(binary.substring(0, 6), 2);
        Integer to = Integer.parseInt(binary.substring(6, 12), 2);
        return new SimpleMove(from, to);
    }

    private String asString(FloatArrayHolder floatArrayHolder) {
        StringBuilder builder = new StringBuilder();
        for (byte item : floatArrayHolder.getArray()) {
            builder.append(Math.round(item));
        }
        return builder.toString();
    }
}
