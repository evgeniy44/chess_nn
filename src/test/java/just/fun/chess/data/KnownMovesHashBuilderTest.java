package just.fun.chess.data;

import chesspresso.game.Game;
import just.fun.chess.MoveHash;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class KnownMovesHashBuilderTest {

    private static final int TOTAL_MOVES_NUMBER = 9;
    private static final int D4_MOVE = 5835;

    @Test
    public void shouldGetHashes() {
        GamesReader gamesReader = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/",
                "test-hashes.pgn");
        KnownMovesHashBuilder hashBuilder = new KnownMovesHashBuilder(gamesReader);

        Set<MoveHash> knownMovesHashes = hashBuilder.getKnownMovesHashes();

        assertThat(knownMovesHashes).hasSize(TOTAL_MOVES_NUMBER);
        Game game = new Game();
        int positionHashCode = game.getPosition().hashCode();
        assertThat(knownMovesHashes).contains(new MoveHash(positionHashCode, D4_MOVE));
    }
}