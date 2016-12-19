package pl.grzeslowski.wykop.classifier.rnn;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RnnConfiguration {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RnnConfiguration.class);

    @Value("${rnn.iterations}")
    private int iterations;
    @Value("${rnn.layers.l0.nout}")
    private int layer0Out;
    @Value("${rnn.regularization}")
    private boolean regularization;
    @Value("${rnn.learningRate}")
    private double learningRate;
    @Value("${rnn.l2}")
    private double l2;
    @Value("${rnn.gradientNormalizationThreshold}")
    private double gradientNormalizationThreshold;
    @Value("${word2vec.hyper.layerSize}")
    private int layerSize;

    @Bean
    MultiLayerNetwork model() {
        log.info("Creating new RNN model...");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(iterations)
                .updater(Updater.RMSPROP)
                .regularization(regularization).l2(l2)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(gradientNormalizationThreshold)
                .learningRate(learningRate)
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .nIn(layerSize)
                        .nOut(200)
                        .activation("softsign").build())
                .layer(1, new GravesLSTM.Builder()
                        .nIn(200)
                        .nOut(100)
                        .activation("softsign").build())
                .layer(2, new RnnOutputLayer.Builder()
                        .activation("softmax")
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .nIn(100)
                        .nOut(layerSize)
                        .build())
                .pretrain(false).backprop(true).build();

        return new MultiLayerNetwork(conf);
    }
}
