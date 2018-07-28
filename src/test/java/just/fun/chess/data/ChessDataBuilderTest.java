package just.fun.chess.data;

import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.data.entity.DataItem;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ChessDataBuilderTest {

    private ChessDataBuilder chessDataBuilder;
    private KnownMovesHashBuilder hashBuilder;

    @BeforeEach
    public void init() {
        MoveConverter moveConverter = new MoveConverter();
        GamesReader gamesReaderHash = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/", "test-items.pgn");
        GamesReader gamesReaderMain = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/", "test-items.pgn");
        hashBuilder = new KnownMovesHashBuilder(gamesReaderHash, moveConverter, new PositionConverter());
        chessDataBuilder = new ChessDataBuilder(moveConverter, new PositionConverter(),
                hashBuilder, gamesReaderMain, false);
    }

    @Test
    public void testSimpleRead() {
        List<DataItem> dataItems = chessDataBuilder.prepareData();
        assertThat(dataItems.size() > 40).isTrue();
    }

    @Test
    @Disabled
    public void shouldNotBeInvalidRecords() {
        List<DataItem> dataItems = chessDataBuilder.prepareData();
        Set<byte[]> inputs = dataItems.stream()
                .map(DataItem::getInput)
                .collect(Collectors.toSet());
        for (byte[] input : inputs) {
            assertThat (dataItems.contains(new DataItem(input, new byte[]{0}))
                    && dataItems.contains(new DataItem(input, new byte[]{1}))).isFalse();
        }
        assertThat(dataItems.size()).isEqualTo(inputs.size());
    }
}