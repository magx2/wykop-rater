package pl.grzeslowski.wykop.classifier.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class FileReader {
    private static final Logger log = LoggerFactory.getLogger(FileReader.class);
    private final List<Charset> charsets = Stream.of("UTF-8", "Windows-1250", "ISO-8859-1", "ISO-8859-2", "US-ASCII")
            .map(Charset::forName)
            .collect(toList());

    @Value("${wykop.postsDir}")
    private String postsDir;

    private Stream<String> readFile(Path path, Charset charset) {
        try (Stream<String> stream = Files.lines(path, charset)) {
            return stream.collect(toList()).stream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<Stream<String>> readFile(Path path) {
        log.trace("Reading file {}.", path.toFile().getName());
        return charsets.stream()
                .map(charset -> {
                    try {
                        return readFile(path, charset);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    public Stream<Path> findAllFilesInDir(File dir) {
        try {
            return Files.walk(dir.toPath())
                    .filter(file -> Files.isRegularFile(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Stream<Stream<String>> readFromStreamOfFileNames(Stream<Path> stream) {
        return stream
                .map(this::readFile)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
