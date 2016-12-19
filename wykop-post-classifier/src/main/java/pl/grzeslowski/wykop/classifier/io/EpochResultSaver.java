package pl.grzeslowski.wykop.classifier.io;

import pl.grzeslowski.wykop.classifier.test.EpochResult;

import java.io.File;
import java.util.Date;

public interface EpochResultSaver {
    File save(EpochResult epochResult, Date date);

    default File save(EpochResult epochResult) {
        return save(epochResult, new Date());
    }
}
