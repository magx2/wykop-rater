package pl.grzeslowski.wykop.classifier.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grzeslowski.wykop.classifier.io.FileReader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

@Configuration
class Word2vecConfiguration {
    private static final Logger log = LoggerFactory.getLogger(Word2vecConfiguration.class);

    @Value("${seed}")
    private int seed;
    @Value("${wykop.postsDir}")
    private File postsDir;
    @Value("${word2vec.models.pathToModel}")
    private File pathToModel;
    @Value("${word2vec.models.pathToWordVectors}")
    private File pathToWordVectors;
    @Value("${word2vec.hyper.minWordFrequency}")
    private int minWordFrequency;
    @Value("${word2vec.hyper.iterations}")
    private int iterations;
    @Value("${word2vec.hyper.layerSize}")
    private int layerSize;
    @Value("${word2vec.hyper.windowsSize}")
    private int windowsSize;

    @Bean
    Word2Vec word2Vec(FileReader fileReader) {
        final Optional<Word2Vec> word2Vec = loadModel();
        if(word2Vec.isPresent()) {
            log.info("Loaded saved word2vec model");
            return word2Vec.get();
        } else {
            try {
                return computeModelAndSave(fileReader);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private Word2Vec computeModelAndSave(FileReader fileReader) throws IOException {
        log.info("Computing word2vec");
        SentenceIterator iterator = new DirSentenceIterator(fileReader, postsDir);
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .iterations(iterations)
                .layerSize(layerSize)
                .seed(seed)
                .windowSize(windowsSize)
                .iterate(iterator)
                .tokenizerFactory(tokenizerFactory)
                .build();

        log.info("Fitting Word2Vec model...");
        vec.fit();

        log.info("Writing model to file...");
        WordVectorSerializer.writeWord2VecModel(vec, pathToModel);

        log.info("Writing word vectors to text file...");
        WordVectorSerializer.writeWordVectors(vec, pathToWordVectors);

        return vec;
    }

    private Optional<Word2Vec> loadModel() {
        log.info("Trying to load word2vec model...");
        try {
            return Optional.of(WordVectorSerializer.readWord2VecModel(pathToModel));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
