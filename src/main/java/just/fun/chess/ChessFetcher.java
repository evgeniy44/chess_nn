package just.fun.chess;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.position.Position;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.fetcher.BaseDataFetcher;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static java.util.Arrays.copyOfRange;

public class ChessFetcher extends BaseDataFetcher {

    public static final int NUM_EXAMPLES = 4200000;
    public static final int NUM_EXAMPLES_TEST = 1200000;
    private PGNReader pgnReader;
    private final String resourcesPath;
    private final String name;
    private Game game;

    public ChessFetcher(String resourcesPath, String name)  {
        this.resourcesPath = resourcesPath;
        this.name = name;
        init();
//
//        int i = 0;
//        while (getNextMove() != null) {
//            i++;
//            if (i % 1000 == 0) {
//                System.out.println("Moves " + i);
//            }
//        }
//        System.out.println("Total moves=" + i);
    }

    private void init() {
        pgnReader = getPgnReader();
        try {
            game = pgnReader.parseGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        game.goBackToLineBegin();
        totalExamples = NUM_EXAMPLES;
    }

    @NotNull
    private PGNReader getPgnReader() {
        try {
            return new PGNReader(new FileInputStream(resourcesPath + name), name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasMore() {
        return game != null;
    }

    public synchronized void fetch(int numExamples) {
        float[][] featureData = new float[numExamples][0];
        float[][] labelData = new float[numExamples][0];
        int actualExamples = 0;

        for (int i = 0; i < numExamples; i++) {
            Position position = getPosition();
            Move move = getNextMove();
            if (move == null) {
                break;
            }

            float[] featureVec = translatePosition(position);
            featureData[actualExamples] = featureVec;

            float[] labelVec = translateMove(move);
            labelData[actualExamples] = labelVec;
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

    private float[] translateMove(Move move) {
        float[] floats = new float[12];
        String fromBinary = String.format("%6s", Integer.toBinaryString(move.getFromSqi())).replace(' ', '0');
        String toBinary = String.format("%6s", Integer.toBinaryString(move.getToSqi())).replace(' ', '0');
        int curentSymbol = 0;
        for (int i = 0; i < fromBinary.length(); i++) {
            floats[curentSymbol] = fromBinary.charAt(i) == '1' ? 1 : 0;
            curentSymbol++;
        }
        for (int i = 0; i < toBinary.length(); i++) {
            floats[curentSymbol] = toBinary.charAt(i) == '1' ? 1 : 0;
            curentSymbol++;
        }
        return floats;
    }

    public static float[] translatePosition(Position position) {
        float[] floats = new float[129];
        int current = 0;
        floats[current] = position.getPlyNumber() % 2;
        current++;
        for (int i = 0; i < 64; i++) {
            floats[current] = position.getColor(i);
            current++;
            floats[current] = position.getPiece(i);
            current++;
        }
        return floats;
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
        init();
    }

    @Override
    public DataSet next() {
        DataSet next = super.next();
        return next;
    }
}
