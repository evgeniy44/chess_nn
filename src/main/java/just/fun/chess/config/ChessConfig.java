package just.fun.chess.config;

import just.fun.chess.ChessTrainer;
import just.fun.chess.data.ChessFetcher;
import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.data.ChessDataBuilder;
import just.fun.chess.data.GamesReader;
import just.fun.chess.data.KnownMovesHashBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChessConfig {

    @Value("${resourcesPath}")
    private String resourcesPath;
    @Value("${trainingFileName}")
    private String trainingFileName;
    @Value("${testFileName}")
    private String testFileName;

    @Bean
    public MoveConverter moveConverter() {
        return new MoveConverter();
    }

    @Bean
    public PositionConverter positionConverter() {
        return new PositionConverter();
    }

    @Bean
    public ChessDataBuilder trainChessDataBuilder(MoveConverter moveConverter, PositionConverter positionConverter,
                                                  KnownMovesHashBuilder trainKnownMovesHashBuilder,
                                                  GamesReader trainHashGamesReader) {
        return new ChessDataBuilder(moveConverter, positionConverter,  trainKnownMovesHashBuilder, trainHashGamesReader,
                true);
    }

    @Bean
    public ChessDataBuilder testChessDataBuilder(MoveConverter moveConverter, PositionConverter positionConverter,
                                                  KnownMovesHashBuilder testKnownMovesHashBuilder,
                                                  GamesReader testHashGamesReader) {
        return new ChessDataBuilder(moveConverter, positionConverter,  testKnownMovesHashBuilder, testHashGamesReader,
                false);
    }

    @Bean
    public GamesReader testGamesReader() {
        return new GamesReader("/Users/yberloh/IdeaProjects/chessnn/", "test.pgn");
    }

    @Bean
    public GamesReader testHashGamesReader() {
        return new GamesReader("/Users/yberloh/IdeaProjects/chessnn/", "test.pgn");
    }

    @Bean
    public GamesReader trainGamesReader() {
        return new GamesReader("/Users/yberloh/IdeaProjects/chessnn/", "train.pgn");
    }

    @Bean
    public GamesReader trainHashGamesReader() {
        return new GamesReader("/Users/yberloh/IdeaProjects/chessnn/", "train.pgn");
    }

    @Bean
    public KnownMovesHashBuilder trainKnownMovesHashBuilder(GamesReader trainGamesReader) {
        return new KnownMovesHashBuilder(trainGamesReader);
    }

    @Bean
    public KnownMovesHashBuilder testKnownMovesHashBuilder(GamesReader testGamesReader) {
        return new KnownMovesHashBuilder(testGamesReader);
    }

    @Bean
    public ChessFetcher trainFetcher(ChessDataBuilder trainChessDataBuilder) {
        return new ChessFetcher(trainChessDataBuilder);
    }

    @Bean
    public ChessFetcher testFetcher(ChessDataBuilder testChessDataBuilder) {
        return new ChessFetcher(testChessDataBuilder);
    }

    @Bean
    public ChessTrainer chess(ChessFetcher trainFetcher, ChessFetcher testFetcher) {
        return new ChessTrainer(trainFetcher, testFetcher);
    }
}
