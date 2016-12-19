package pl.grzeslowski.wykop.classifier.io;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface IoService {
    Stream<Path> findAllFilesInDir(File dir);

    Optional<Stream<String>> readFile(Path path);
}
