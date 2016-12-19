package pl.grzeslowski.wykop.classifier.data;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public interface DataProvider {
    DataSetIterator newTrainData();
    DataSetIterator newTestData();
}
