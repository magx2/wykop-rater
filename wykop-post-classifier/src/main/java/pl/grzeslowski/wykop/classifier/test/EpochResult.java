package pl.grzeslowski.wykop.classifier.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.grzeslowski.wykop.classifier.rnn.RnnProperties;

import java.io.File;

public class EpochResult {
    private final int epoch;
    private final TestOutput testOutput;
    private final RnnProperties rnnProperties;
    private final File modelFile;

    EpochResult(
            @JsonProperty("epoch") int epoch,
            @JsonProperty("testOutput") TestOutput testOutput,
            @JsonProperty("rnnProperties") RnnProperties rnnProperties,
            @JsonProperty("modelFile") File modelFile) {
        this.epoch = epoch;
        this.testOutput = testOutput;
        this.rnnProperties = rnnProperties;
        this.modelFile = modelFile;
    }

    public int getEpoch() {
        return epoch;
    }

    public RnnProperties getRnnProperties() {
        return rnnProperties;
    }

    public TestOutput getTestOutput() {
        return testOutput;
    }

    public File getModelFile() {
        return modelFile;
    }
}
