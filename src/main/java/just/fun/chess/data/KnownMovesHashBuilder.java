package just.fun.chess.data;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.position.Position;
import just.fun.chess.MoveHash;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class KnownMovesHashBuilder {

    private final GamesReader gamesReader;

    public KnownMovesHashBuilder(GamesReader gamesReader) {
        this.gamesReader = gamesReader;
    }

    public Set<MoveHash> getKnownMovesHashes() {
        Set<MoveHash> hashes = new HashSet<>();
        for (Game game : gamesReader.readGames()) {
            hashes.addAll(getGameMovesHashes(game));
        }
        return hashes;
    }

    private Set<MoveHash> getGameMovesHashes(Game game) {
        Set<MoveHash> gameMovesHashes = new HashSet<>();
        while (true) {
            Position position = game.getPosition();
            if (position == null) {
                break;
            }
            int positionHash = position.hashCode();
            Move move = game.getNextMove();
            if (move == null) {
                break;
            }
            int moveHash = move.getShortMoveDesc();
            gameMovesHashes.add(new MoveHash(positionHash, moveHash));
            game.goForward();
        }
        return gameMovesHashes;
    }
}
