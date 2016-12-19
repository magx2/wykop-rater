package pl.grzeslowski.wykop.classifier.io;

import pl.grzeslowski.wykop.posts.Site;

import java.util.stream.Stream;

public interface IoService {
    Stream<Site> findAllSites();
}
