package pl.grzeslowski.wykop.classifier.rnn;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RnnProperties {
    private final int iterations;
    private final boolean regularization;
    private final double learningRate;
    private final double l2;
    private final double gradientNormalizationThreshold;

    RnnProperties(
            @JsonProperty("iterations") int iterations,
            @JsonProperty("regularization") boolean regularization,
            @JsonProperty("learningRate") double learningRate,
            @JsonProperty("l2") double l2,
            @JsonProperty("gradientNormalizationThreshold") double gradientNormalizationThreshold) {
        this.iterations = iterations;
        this.regularization = regularization;
        this.learningRate = learningRate;
        this.l2 = l2;
        this.gradientNormalizationThreshold = gradientNormalizationThreshold;
    }

    public int getIterations() {
        return iterations;
    }

    public boolean isRegularization() {
        return regularization;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getL2() {
        return l2;
    }

    public double getGradientNormalizationThreshold() {
        return gradientNormalizationThreshold;
    }
}
