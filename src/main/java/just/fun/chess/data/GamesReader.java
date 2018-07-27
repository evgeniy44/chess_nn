package just.fun.chess.data;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GamesReader {

    private static final int RETRIES_COUNT = 3;

    private final PGNReader pgnReader;

    public GamesReader(String resourcesPath, String name) {
        pgnReader = getPgnReader(resourcesPath, name);
    }

    private PGNReader getPgnReader(String resourcesPath, String name) {
        try {
            return new PGNReader(new FileInputStream(resourcesPath + name), name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized List<Game> readGames() {
        log.info("Loading list of games...");
        List<Game> games = new ArrayList<>();
        Game game = readNextGame();
        while (game != null) {
            game.goBackToLineBegin();
            games.add(game);
            game = readNextGame();
        }
        return games;
    }

    private Game readNextGame() {
        for (int i = 0; i < RETRIES_COUNT; i++) {
            try {
                return pgnReader.parseGame();
            } catch (Exception e) {
                System.err.println("Couldn't read next game " + e.getMessage());
                if (i == 2) {
                    throw new IllegalStateException("Retries failed", e);
                }
            }
        }
        return null;
    }
}
