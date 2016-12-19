package pl.grzeslowski.wykop.classifier.io;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.File;
import java.util.Date;

public interface MultiLayerNetworkSaver {
    File save(MultiLayerNetwork model, Date timestamp);

    default File save(MultiLayerNetwork model) {
        return save(model, new Date());
    }
}
