package pl.grzeslowski.wykop.classifier.test;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public interface Trainer {
    MultiLayerNetwork trainAndTest();
}
