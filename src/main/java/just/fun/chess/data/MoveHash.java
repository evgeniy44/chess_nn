package just.fun.chess.data;

import lombok.Data;

@Data
public class MoveHash {
    private final int positionHashCode;
    private final int moveHashCode;
}
