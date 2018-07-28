package just.fun.chess.data;

import chesspresso.game.Game;
import chesspresso.move.Move;
import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.board.SimpleMove;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class KnownMovesHashBuilder {

    private final GamesReader gamesReader;
    private final MoveConverter moveConverter;
    private final PositionConverter positionConverter;

    public KnownMovesHashBuilder(GamesReader gamesReader, MoveConverter moveConverter, PositionConverter positionConverter) {
        this.gamesReader = gamesReader;
        this.moveConverter = moveConverter;
        this.positionConverter = positionConverter;
    }

    public Set<Integer> getKnownMovesHashes() {
        Set<Integer> hashes = new HashSet<>();
        for (Game game : gamesReader.readGames()) {
            hashes.addAll(getGameMovesHashes(game));
        }
        return hashes;
    }

    private Set<Integer> getGameMovesHashes(Game game) {
        Set<Integer> gameMovesHashes = new HashSet<>();
        while (game.hasNextMove()) {
            byte[] moveBytes = moveConverter.convert(new SimpleMove(
                    Move.getFromSqi(game.getNextMove().getShortMoveDesc()), Move.getToSqi(game.getNextMove().getShortMoveDesc()), game.getPosition().getToPlay())).getArray();
            byte[] positionBytes = positionConverter.convert(game.getPosition()).getArray();
            gameMovesHashes.add(Arrays.hashCode(ArrayUtils.addAll(positionBytes, moveBytes)));
            game.goForward();
        }
        return gameMovesHashes;
    }
}
