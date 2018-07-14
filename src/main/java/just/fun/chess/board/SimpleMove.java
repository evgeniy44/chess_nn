package just.fun.chess.board;

import chesspresso.move.Move;
import lombok.Data;

@Data
public class SimpleMove {
    private final int from;
    private final int to;

    public SimpleMove(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public SimpleMove(Move move) {
        this.from = move.getFromSqi();
        this.to = move.getToSqi();
    }
}
