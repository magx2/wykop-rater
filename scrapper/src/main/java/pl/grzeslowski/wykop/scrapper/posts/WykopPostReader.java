package pl.grzeslowski.wykop.scrapper.posts;

import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
class WykopPostReader implements PostReader {
    private static final SimpleDateFormat POST_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class ParentElemMainPost {
        private final Html.Element element;
        private final Optional<Html.Element> postElement;

        private ParentElemMainPost(Html.Element element, Optional<Html.Element> postElement) {
            this.element = element;
            this.postElement = postElement;
        }
    }

    @Override
    public Stream<Post> readPosts(Html html) {
        return html
                .select("li.iC") // Selecting list of all main comments
                .map(element -> new ParentElemMainPost(element, element.select("div").findFirst()))
                .filter(x -> x.postElement.isPresent())
                .map(this::parse);
    }

    private Post parse(ParentElemMainPost parentElemMainPost) {
        assert parentElemMainPost.postElement.isPresent();
        final Html.Element element = parentElemMainPost.postElement.get();

        // parse sub posts
        final List<Post> postList = parentElemMainPost.element
                .select("ul.sub li div.wblock.lcontrast.dC  ")
                .map(this::parse)
                .collect(toList());

        return new Post(
                parseAuthor(element),
                parsePublishDate(element),
                parseScore(element),
                parseContent(element),
                postList
        );
    }

    private Post parse(Html.Element element) {
        return new Post(
                parseAuthor(element),
                parsePublishDate(element),
                parseScore(element),
                parseContent(element)
        );
    }

    private String parseAuthor(Html.Element element) {
        return element.select("a.showProfileSummary b")
                .findFirst()
                .map(Html.Element::text)
                .orElseThrow(() -> new IllegalStateException("Could not parse author name!"));
    }

    private Date parsePublishDate(Html.Element element) {
        return element.select("time")
                .findFirst()
                .map(e -> e.attribute("datetime"))
                .map(parseDate())
                .orElseThrow(() -> new IllegalStateException("Could not parse date!"));
    }

    private Score parseScore(Html.Element element) {
        return element.select("p.Vc")
                .findFirst()
                .map(this::parseScoreDirectly)
                .orElseThrow(() -> new IllegalStateException("Could not parse score!"));
    }

    private String parseContent(Html.Element element) {
        return element.select("div.text p")
                .findFirst()
                .map(Html.Element::text)
                .orElseThrow(() -> new IllegalStateException("Could not parse content!"));

    }

    private Function<String, Date> parseDate() {
        return date -> {
            try {
                return POST_DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(format("Cannot parse %s!", date), e);
            }
        };
    }

    private Score parseScoreDirectly(Html.Element element) {
        int up = parseInt(element.attribute("data-vcp"));
        int down = abs(parseInt(element.attribute("data-vcm")));
        return new Score(up, down);
    }
}
