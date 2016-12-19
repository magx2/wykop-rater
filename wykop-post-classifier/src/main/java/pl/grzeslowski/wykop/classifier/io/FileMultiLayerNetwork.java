package pl.grzeslowski.wykop.classifier.io;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
class FileMultiLayerNetwork implements MultiLayerNetworkLoader, MultiLayerNetworkSaver {
    private static final Logger log = LoggerFactory.getLogger(FileMultiLayerNetwork.class);
    private FileReader fileReader;
    private final File dirToSave;
    private final String modelPrefix;
    private final String modelSuffix;

    @Autowired
    FileMultiLayerNetwork(FileReader fileReader,
                          @Value("${fileMultiLayerNetwork.dirToSave}") File dirToSave,
                          @Value("${fileMultiLayerNetwork.modelPrefix}") String modelPrefix,
                          @Value("${fileMultiLayerNetwork.modelSuffix}") String modelSuffix) {
        this.fileReader = fileReader;
        this.dirToSave = dirToSave;
        checkArgument(dirToSave.exists());
        checkArgument(dirToSave.isDirectory());
        this.modelPrefix = modelPrefix;
        this.modelSuffix = modelSuffix;
    }

    @Override
    public void save(MultiLayerNetwork model, Date date) {
        String fileName = String.format("%s%s%s", modelPrefix, date.getTime(), modelSuffix);
        File modelFile = createModelFile(fileName);
        try {
            log.info("Saving model {}", modelFile.getAbsoluteFile());
            ModelSerializer.writeModel(model, modelFile, true);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save model to file " + fileName + "!", e);
        }
    }

    private File createModelFile(String fileName) {
        return new File(dirToSave.getAbsolutePath() + File.separator + fileName);
    }

    @Override
    public Optional<MultiLayerNetwork> load() {
        return fileReader.findAllFilesInDir(dirToSave)
                .sorted()
                .findFirst()
                .map(Path::toFile)
                .map(this::restoreMultiLayerNetwork);
    }

    private MultiLayerNetwork restoreMultiLayerNetwork(File modelFile) {
        try {
            return ModelSerializer.restoreMultiLayerNetwork(modelFile, true);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read model from " + modelFile.getAbsolutePath() + "!", e);
        }
    }
}
