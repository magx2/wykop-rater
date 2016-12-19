package pl.grzeslowski.wykop.classifier.test;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.classifier.data.DataProvider;
import pl.grzeslowski.wykop.classifier.io.EpochResultSaver;
import pl.grzeslowski.wykop.classifier.io.MultiLayerNetworkSaver;
import pl.grzeslowski.wykop.classifier.rnn.RnnProperties;

import javax.inject.Provider;
import java.io.File;
import java.util.stream.IntStream;

@Service
class TrainerImpl implements Trainer {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TrainerImpl.class);

    private final Provider<MultiLayerNetwork> modelProvider;
    private final MultiLayerNetworkSaver networkSaver;
    private final DataProvider dataProvider;
    private final EpochResultSaver epochResultSaver;
    private final IterationListener[] iterationListenerList;
    private final RnnProperties rnnProperties;

    @Value("${rnn.maxWordsInDialog}")
    private int maxWordsInDialog;
    @Value("${test.batchSize}")
    private int batchSize;
    @Value("${test.epochs}")
    private int epochs;
    @Value("${test.suppressWarnings}")
    private boolean suppressWarnings;
    @Value("${word2vec.hyper.layerSize}")
    private int layerSize;


    @Autowired
    public TrainerImpl(Provider<MultiLayerNetwork> modelProvider,
                       MultiLayerNetworkSaver networkSaver,
                       DataProvider dataProvider,
                       EpochResultSaver epochResultSaver,
                       IterationListener[] iterationListenerList,
                       RnnProperties rnnProperties) {
        this.modelProvider = modelProvider;
        this.networkSaver = networkSaver;
        this.dataProvider = dataProvider;
        this.epochResultSaver = epochResultSaver;
        this.iterationListenerList = iterationListenerList;
        this.rnnProperties = rnnProperties;
    }

    @Override
    public MultiLayerNetwork trainAndTest() {
        final MultiLayerNetwork net = modelProvider.get();

        log.info("Initializing model");
        net.init();
        net.setListeners(iterationListenerList);

        IntStream.range(1, epochs + 1)
                .mapToObj(epoch -> trainAndTestEpoch(net, epoch))
                .reduce((a, b) -> b)
                .ifPresent(epochResultSaver::save);

        return net;
    }

    private EpochResult trainAndTestEpoch(MultiLayerNetwork net, int epoch) {
        final DataSetIterator train = dataProvider.newTrainData();
        final DataSetIterator test = dataProvider.newTestData();

        log.info("Starting learning, epoch {}", epoch);
        net.fit(train);

        final File modelFile = networkSaver.save(net);

        log.info("Starting evaluation:");

        Evaluation evaluation = new Evaluation();
        while (test.hasNext()) {
            DataSet t = test.next();
            INDArray features = t.getFeatures();
            INDArray labels = t.getLabels();
            INDArray inMask = t.getFeaturesMaskArray();
            INDArray outMask = t.getLabelsMaskArray();
            INDArray predicted = net.output(features, false, inMask, outMask);

            evaluation.evalTimeSeries(labels, predicted, outMask);
        }
        log.info("Evaluation output:\n{}", evaluation.stats(suppressWarnings));

        return new EpochResult(epoch, new TestOutput(evaluation), rnnProperties, modelFile);
    }
}
