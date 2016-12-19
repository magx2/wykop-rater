package pl.grzeslowski.wykop.classifier.io;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.Optional;

public interface MultiLayerNetworkLoader {
    Optional<MultiLayerNetwork> load();
}
