package pl.grzeslowski.wykop.classifier.word2vec;

import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import pl.grzeslowski.wykop.classifier.io.IoService;
import pl.grzeslowski.wykop.posts.Post;
import pl.grzeslowski.wykop.posts.Site;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

class DirSentenceIterator implements SentenceIterator {
    private final IoService ioService;
    private Iterator<Site> iterator;
    private SentencePreProcessor preProcessor;

    DirSentenceIterator(IoService ioService) {
        this.ioService = checkNotNull(ioService);
        initIterator();
    }

    private void initIterator() {
        iterator = ioService.findAllSites().iterator();
    }

    @Override
    public String nextSentence() {
        checkArgument(hasNext(), "Iterator does not have next elem!");
        final Site site = iterator.next();
        return format("%s %s %s", site.getAuthor(), site.getContent(), transformPostToString(site.getPosts()));
    }

    private String transformPostToString(List<Post> posts) {
        return posts .stream()
                .map(p -> format("%s %s %s", p.getAuthor(), p.getContent(), transformPostToString(p.getResponses())))
                .collect(joining(" "));
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
