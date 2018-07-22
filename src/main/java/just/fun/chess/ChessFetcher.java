package just.fun.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.position.Position;
import just.fun.chess.board.MoveConverter;
import just.fun.chess.board.PositionConverter;
import just.fun.chess.board.SimpleMove;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.fetcher.BaseDataFetcher;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.Arrays.copyOfRange;

public class ChessFetcher extends BaseDataFetcher {

    private PGNReader pgnReader;
    private final MoveConverter moveConverter;
    private final PositionConverter positionConverter;
    private final String resourcesPath;
    private final String name;
    private Game game;
    private List<TrainingExample> trainingExamples;
    private Set<HashEntity> hashTable = new HashSet<>();
    int skipped = 0;

    public ChessFetcher(MoveConverter moveConverter, PositionConverter positionConverter, String resourcesPath, String name)  {
        this.moveConverter = moveConverter;
        this.positionConverter = positionConverter;
        this.resourcesPath = resourcesPath;
        this.name = name;
        init();
    }

    private void init() {
        reloadPgn();
        populateHashTable();
        reloadPgn();
        trainingExamples = new ArrayList<>();
        prepareTrainingData();
        totalExamples = trainingExamples.size();
    }

    private void prepareTrainingData() {
        while (true) {
            Position position = getPosition();
            if (position == null) {
                break;
            }
            int positionHash = position.hashCode();
            byte[] positionVec = positionConverter.convert(position).getArray();
            loadRandomMoves(position, positionHash, positionVec);
            Move move = getNextMove();
            if (move == null) {
                break;
            }
            byte[] actualMoveVec = moveConverter.convert(new SimpleMove(move, position)).getArray();
            trainingExamples.add(new TrainingExample(ArrayUtils.addAll(positionVec, actualMoveVec), new byte[]{1}));
            if (trainingExamples.size() % 10000 == 0) {
                System.out.println("Added " + trainingExamples.size() + ", skipped: " + skipped);
            }
        }
    }

    private void loadRandomMoves(Position position, int positionHash, byte[] positionVec) {
        for (int i = 0; i < 3; i++) {
            if (position.getAllMoves().length == 0) {
                break;
            }
            short possibleMove = position.getAllMoves()[new Random().nextInt(position.getAllMoves().length)];
            if (hashTable.contains(new HashEntity(positionHash, possibleMove))) {
                skipped++;
                continue;
            }
            byte[] possibleMoveVec = moveConverter.convert(new SimpleMove(
                    Move.getFromSqi(possibleMove), Move.getToSqi(possibleMove), position.getToPlay())).getArray();
            trainingExamples.add(new TrainingExample(
                    ArrayUtils.addAll(positionVec, possibleMoveVec),
                    new byte[]{0}));
        }
    }

    private void populateHashTable() {
        log.info("Calculating hash codes for known positions/moves...");
        while (true) {
            Position position = getPosition();
            if (position == null) {
                break;
            }
            int positionHash = position.hashCode();
            Move move = getNextMove();
            if (move == null) {
                break;
            }
            int moveHash = move.getShortMoveDesc();
            hashTable.add(new HashEntity(positionHash, moveHash));
        }
    }

    private void reloadPgn() {
        log.info("Loading PGN file...");
        pgnReader = getPgnReader();
        try {
            game = pgnReader.parseGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        game.goBackToLineBegin();
    }

    @NotNull
    private PGNReader getPgnReader() {
        try {
            return new PGNReader(new FileInputStream(resourcesPath + name), name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getExamplesNumber() {
        return trainingExamples.size();
    }

    @Override
    public boolean hasMore() {
        return cursor < trainingExamples.size();
    }

    public synchronized void fetch(int numExamples) {
        float[][] featureData = new float[numExamples][0];
        float[][] labelData = new float[numExamples][0];
        int actualExamples = 0;

        for (int i = 0; actualExamples < numExamples; cursor++) {
            featureData[actualExamples] = toFloatArray(trainingExamples.get(cursor).getInput());
            labelData[actualExamples] = toFloatArray(trainingExamples.get(cursor).getScore());
            actualExamples++;

        }
        if (actualExamples < numExamples) {
            featureData = copyOfRange(featureData, 0, actualExamples);
            labelData = copyOfRange(labelData, 0, actualExamples);
        }

        INDArray features = Nd4j.create(featureData);
        INDArray labels = Nd4j.create(labelData);
        curr = new DataSet(features, labels);
    }

    private float[] toFloatArray(byte[] byteArray) {
        float[] doubles = new float[byteArray.length];

        for(int i = 0; i < doubles.length; ++i) {
            doubles[i] = byteArray[i];
        }

        return doubles;
    }

    private Position getPosition() {
        return game.getPosition();
    }

    private Move getNextMove() {
        if (game.hasNextMove()) {
            Move move = game.getNextMove();
            game.goForward();
            return move;
        }
        game = readNextGame();
        if (game == null) {
            return null;
        }
        game.goBackToLineBegin();
        return getNextMove();
    }

    private Game readNextGame() {
        for (int i = 0; i < 3; i++) {
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

    @Override
    public void reset() {
        cursor = 0;
        curr = null;
    }

    @Override
    public DataSet next() {
        return super.next();
    }
}
