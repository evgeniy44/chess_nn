package just.fun.chess.data;

import chesspresso.game.Game;
import chesspresso.move.Move;
import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.board.SimpleMove;
import just.fun.chess.data.entity.DataItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
public class ChessDataBuilder {

    private int skipped = 0;
    private static final int RANDOM_MOVES_COUNT = 3;

    private final MoveConverter moveConverter;
    private final PositionConverter positionConverter;
    private final KnownMovesHashBuilder hashBuilder;
    private final GamesReader gamesReader;
    private final boolean shuffle;

    public ChessDataBuilder(MoveConverter moveConverter, PositionConverter positionConverter,
                            KnownMovesHashBuilder hashBuilder, GamesReader gamesReader, boolean shuffle) {
        this.moveConverter = moveConverter;
        this.positionConverter = positionConverter;
        this.hashBuilder = hashBuilder;
        this.gamesReader = gamesReader;
        this.shuffle = shuffle;
    }

    public synchronized List<DataItem> prepareData() {
        log.info("Start preparing data items...");
        Set<Integer> knownMovesHashes = hashBuilder.getKnownMovesHashes();

        List<DataItem> dataItems = new ArrayList<>();
        for (Game game : gamesReader.readGames()) {
            dataItems.addAll(getDataItems(game, knownMovesHashes));
            log.info("Prepared: " + dataItems.size() + ", skipped: " + skipped);
        }
        if (shuffle) {
            log.info("Shuffling data items...");
            Collections.shuffle(dataItems);
        }
        return dataItems;
    }

    private List<DataItem> getDataItems(Game game, Set<Integer> knownMovesHashes) {
        List<DataItem> items = new ArrayList<>();
        game.goBackToLineBegin();
        while (game.getNextMove() != null) {
            items.addAll(getCurrentPositionItems(game, knownMovesHashes));
            game.goForward();
        }
        return items;
    }

    private List<DataItem> getCurrentPositionItems(Game game, Set<Integer> knownMovesHashes) {
        List<DataItem> items = new ArrayList<>(getRandomMovesData(game, knownMovesHashes));
        byte[] positionVec = positionConverter.convert(game.getPosition()).getArray();
        byte[] actualMoveVec = moveConverter.convert(new SimpleMove(game.getNextMove(), game.getPosition())).getArray();
        items.add(new DataItem(ArrayUtils.addAll(positionVec, actualMoveVec), new byte[]{1}));
        return items;
    }

    private List<DataItem> getRandomMovesData(Game game, Set<Integer> knownMovesHashes) {
        List<DataItem> dataItems = new ArrayList<>();
        for (int i = 0; i < RANDOM_MOVES_COUNT; i++) {
            if (game.getPosition().getAllMoves().length == 0) {
                break;
            }
            short possibleMove = game.getPosition().getAllMoves()[new Random().nextInt(game.getPosition().getAllMoves().length)];
            byte[] possibleMoveBytes = moveConverter.convert(new SimpleMove(
                    Move.getFromSqi(possibleMove), Move.getToSqi(possibleMove), game.getPosition().getToPlay())).getArray();
            byte[] inputBytes = ArrayUtils.addAll(positionConverter.convert(game.getPosition()).getArray(), possibleMoveBytes);
            if (knownMovesHashes.contains(Arrays.hashCode(inputBytes))) {
                skipped++;
                continue;
            }
            dataItems.add(new DataItem(inputBytes, new byte[]{0}));
        }
        return dataItems;
    }
}