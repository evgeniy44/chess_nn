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
import java.util.List;

import static java.util.Arrays.copyOfRange;

public class ChessFetcher extends BaseDataFetcher {

    private PGNReader pgnReader;
    private final MoveConverter moveConverter;
    private final PositionConverter positionConverter;
    private final String resourcesPath;
    private final String name;
    private Game game;
    private List<TrainingExample> trainingExamples;

    public ChessFetcher(MoveConverter moveConverter, PositionConverter positionConverter, String resourcesPath, String name)  {
        this.moveConverter = moveConverter;
        this.positionConverter = positionConverter;
        this.resourcesPath = resourcesPath;
        this.name = name;
        init();
    }

    private void init() {
        pgnReader = getPgnReader();
        trainingExamples = new ArrayList<>();
        try {
            game = pgnReader.parseGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        game.goBackToLineBegin();


        while (true) {
            Position position = getPosition();
            if (position == null) {
                break;
            }
            byte[] positionVec = positionConverter.convert(position).getArray();
            Move move = getNextMove();
            if (move == null) {
                break;
            }
            byte[] actualMoveVec = moveConverter.convert(new SimpleMove(move)).getArray();

            trainingExamples.add(new TrainingExample(ArrayUtils.addAll(positionVec, actualMoveVec), new byte[]{1}));

            int iter = 5;
            for (short possibleMove : position.getAllMoves()) {
                if (iter == 0) {
                    break;
                }
                if (possibleMove == move.getShortMoveDesc()) {
                    continue;
                }
                byte[] possibleMoveVec = moveConverter.convert(new SimpleMove(Move.getFromSqi(possibleMove), Move.getToSqi(possibleMove))).getArray();
                trainingExamples.add(new TrainingExample(
                        ArrayUtils.addAll(positionVec, possibleMoveVec),
                        new byte[]{0}));
                iter--;
            }
            if (trainingExamples.size() % 10000 == 0) {
                System.out.println("Added " + trainingExamples.size());
            }
        }

        totalExamples = trainingExamples.size();
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
//        init();
    }

    @Override
    public DataSet next() {
        DataSet next = super.next();
        return next;
    }
}
