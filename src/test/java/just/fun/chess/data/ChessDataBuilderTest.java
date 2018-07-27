package just.fun.chess.data;

import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChessDataBuilderTest {

    @Test
    public void test() {
        MoveConverter moveConverter = new MoveConverter();
        GamesReader gamesReaderHash = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/", "test-hashes.pgn");
        GamesReader gamesReaderMain = new GamesReader("/Users/yberloh/IdeaProjects/chessnn/src/test/resources/", "test-hashes.pgn");
        ChessDataBuilder chessDataBuilder = new ChessDataBuilder(moveConverter, new PositionConverter(),
                new KnownMovesHashBuilder(gamesReaderHash), gamesReaderMain, false);

        List<DataItem> dataItems = chessDataBuilder.prepareData();
        assertThat(dataItems.size() > 40).isTrue();
    }
}