package pl.grzeslowski.wykop.scrapper.posts;

import pl.grzeslowski.wykop.scrapper.html.Html;

import java.util.stream.Stream;

public interface PostReader {
    Stream<Post> readPosts(Html html);
}
