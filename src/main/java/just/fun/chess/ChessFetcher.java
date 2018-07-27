package just.fun.chess;

import just.fun.chess.data.ChessDataBuilder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.fetcher.BaseDataFetcher;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

import static java.util.Arrays.copyOfRange;

public class ChessFetcher extends BaseDataFetcher {

    private final ChessDataBuilder chessDataBuilder;
    private List<DataItem> dataItems;

    public ChessFetcher(ChessDataBuilder chessDataBuilder) {
        this.chessDataBuilder = chessDataBuilder;
        init();
    }

    private void init() {
        dataItems = chessDataBuilder.prepareData();
        totalExamples = dataItems.size();
    }

    public int getExamplesNumber() {
        return dataItems.size();
    }

    @Override
    public boolean hasMore() {
        return cursor < dataItems.size();
    }

    public synchronized void fetch(int numExamples) {
        float[][] featureData = new float[numExamples][0];
        float[][] labelData = new float[numExamples][0];
        int actualExamples = 0;

        for (; actualExamples < numExamples; cursor++) {
            if (cursor >= dataItems.size()) {
                break;
            }
            featureData[actualExamples] = toFloatArray(dataItems.get(cursor).getInput());
            labelData[actualExamples] = toFloatArray(dataItems.get(cursor).getScore());
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
