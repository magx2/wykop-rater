package pl.grzeslowski.wykop.classifier.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.deeplearning4j.eval.Evaluation;

public class TestOutput {
    private final double accuracy;
    private final double precision;
    private final double recall;
    private final double f1;

    TestOutput(Evaluation eval) {
        this(eval.accuracy(), eval.precision(), eval.recall(), eval.f1());
    }

    TestOutput(
            @JsonProperty("accuracy") double accuracy,
            @JsonProperty("precision") double precision,
            @JsonProperty("recall") double recall,
            @JsonProperty("f1") double f1) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1() {
        return f1;
    }
}
