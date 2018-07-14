package just.fun.chess;

import org.nd4j.linalg.dataset.api.iterator.BaseDatasetIterator;
import org.nd4j.linalg.dataset.api.iterator.fetcher.DataSetFetcher;

public class ChessDataSetIterator extends BaseDatasetIterator {
    public ChessDataSetIterator(int batch, int numExamples, DataSetFetcher fetcher) {
        super(batch, numExamples, fetcher);
    }
}
