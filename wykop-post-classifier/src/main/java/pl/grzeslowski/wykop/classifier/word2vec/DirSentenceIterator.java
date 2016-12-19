package pl.grzeslowski.wykop.classifier.word2vec;

import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import pl.grzeslowski.wykop.classifier.io.IoService;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class DirSentenceIterator implements SentenceIterator {
    private final IoService ioService;
    private Iterator<Path> iterator;
    private SentencePreProcessor preProcessor;

    DirSentenceIterator(IoService ioService) {
        this.ioService = checkNotNull(ioService);
        initIterator();
    }

    private void initIterator() {
        iterator = ioService.findAllPostsFiles().collect(Collectors.toSet()).iterator();
    }

    @Override
    public String nextSentence() {
        checkArgument(hasNext(), "Iterator does not have next elem!");
        final Path toRead = iterator.next();
        final Optional<Stream<String>> stringStream = ioService.readFile(toRead);
        if (!stringStream.isPresent()) {
            return nextSentence();
        } else {
            return stringStream.get()
                    .map(line -> line.replaceAll("\\{", " "))
                    .map(line -> line.replaceAll("}", " "))
                    .collect(Collectors.joining("\n"));
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void reset() {
        initIterator();
    }

    @Override
    public void finish() {
        iterator = null;
    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return preProcessor;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }
}
