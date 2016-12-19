package pl.grzeslowski.wykop.classifier.io;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.Date;

public interface MultiLayerNetworkSaver {
    void save(MultiLayerNetwork model, Date timestamp);

    default void save(MultiLayerNetwork model) {
        save(model, new Date());
    }
}
