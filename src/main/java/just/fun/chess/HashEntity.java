package just.fun.chess;

import lombok.Data;

@Data
public class HashEntity {
    private final int positionHashCode;
    private final int moveHashCode;
}
