package just.fun.chess.data;

import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KnownMovesHashBuilderTest {

    private static final int TOTAL_MOVES_NUMBER = 9;

    @Test
    public void shouldGetHashes() {
        GamesReader gamesReader = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/",
                "test-hashes.pgn");
        KnownMovesHashBuilder hashBuilder = new KnownMovesHashBuilder(gamesReader, new MoveConverter(), new PositionConverter());
        Set<Integer> knownMovesHashes = hashBuilder.getKnownMovesHashes();
        assertThat(knownMovesHashes).hasSize(TOTAL_MOVES_NUMBER);
    }
}