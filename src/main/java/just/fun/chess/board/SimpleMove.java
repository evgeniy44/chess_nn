package just.fun.chess.board;

import chesspresso.move.Move;
import chesspresso.position.Position;
import lombok.Data;

@Data
public class SimpleMove {
    private final int from;
    private final int to;
    private int player;

    public SimpleMove(int from, int to, int player) {
        this.from = from;
        this.to = to;
        this.player = player;
    }

    public SimpleMove(Move move, Position position) {
        this.from = move.getFromSqi();
        this.to = move.getToSqi();
        this.player = position.getToPlay();
    }
}
