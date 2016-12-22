package pl.grzeslowski.wykop.classifier.test;

import com.google.common.base.Preconditions;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import java.io.File;

@Configuration
class IterationListenerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(IterationListenerConfiguration.class);
    private final int printIterations;
    private final File fileStatsStorage;
    private FileStatsStorage statsStorage;

    IterationListenerConfiguration(@Value("${iterationListener.printIterations}") int printIterations,
                                   @Value("${iterationListener.fileStatsStorage}") File fileStatsStorage) {
        this.printIterations = printIterations;
        Preconditions.checkArgument(printIterations > 0, "printIterations = " + printIterations);
        this.fileStatsStorage = fileStatsStorage;
    }

    @Bean
    ScoreIterationListener scoreIterationListener() {
        return new ScoreIterationListener(printIterations);
    }

    @Bean
    @Profile("uiserver")
    StatsListener statsListener() {
        UIServer uiServer = UIServer.getInstance();
        if(fileStatsStorage.delete()) {
            log.info("Deleted previous file with stats storage");
        }
        statsStorage = new FileStatsStorage(fileStatsStorage);
        uiServer.attach(statsStorage);
        return new StatsListener(statsStorage);
    }

    @PreDestroy
    private void detach() {
        if(statsStorage != null) {
            UIServer uiServer = UIServer.getInstance();
            uiServer.detach(statsStorage);
            statsStorage = null;
        }
    }
}
