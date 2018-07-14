package just.fun.chess.config;

import just.fun.chess.Chess;
import just.fun.chess.ChessFetcher;
import just.fun.chess.board.MoveConverter;
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
    public ChessFetcher trainFetcher(MoveConverter moveConverter) {
        return new ChessFetcher(moveConverter, resourcesPath, trainingFileName);
    }

    @Bean
    public ChessFetcher testFetcher(MoveConverter moveConverter) {
        return new ChessFetcher(moveConverter, resourcesPath, testFileName);
    }

    @Bean
    public Chess chess(ChessFetcher trainFetcher, ChessFetcher testFetcher) {
        return new Chess(trainFetcher, testFetcher);
    }
}