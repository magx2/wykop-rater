package pl.grzeslowski.wykop.classifier.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.classifier.test.EpochResult;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

@Service
class JsonEpochResultSaver implements EpochResultSaver {
    private static final Logger log = LoggerFactory.getLogger(JsonEpochResultSaver.class);
    private final ObjectMapper objectMapper;
    private final File dirToSave;
    private final String modelPrefix;
    private final String modelSuffix;

    @Autowired
    JsonEpochResultSaver(ObjectMapper objectMapper,
                         @Value("${fileMultiLayerNetwork.dirToSave}") File dirToSave,
                         @Value("${fileMultiLayerNetwork.modelPrefix}") String modelPrefix,
                         @Value("${fileMultiLayerNetwork.modelSuffix}") String modelSuffix) {
        this.objectMapper = objectMapper;
        this.dirToSave = dirToSave;
        checkArgument(dirToSave.exists());
        checkArgument(dirToSave.isDirectory());
        this.modelPrefix = modelPrefix;
        this.modelSuffix = modelSuffix;
    }

    @Override
    public File save(EpochResult epochResult, Date date) {
        String fileName = createFileName(epochResult, date);
        File epochFile = createModelFile(fileName);
        try {
            log.info("Saving epoch#{} {}", epochResult.getEpoch(), epochFile.getAbsoluteFile());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(epochFile, epochResult);
            return epochFile;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save epoch to file " + fileName + "!", e);
        }
    }

    private String createFileName(EpochResult epochResult, Date date) {
        return String.format("%s%s-%s-%s%s",
                modelPrefix,
                epochResult.getTestOutput().getAccuracy(),
                epochResult.getTestOutput().getPrecision(),
                date.getTime(),
                modelSuffix);
    }

    private File createModelFile(String fileName) {
        return new File(dirToSave.getAbsolutePath() + File.separator + fileName);
    }
}
