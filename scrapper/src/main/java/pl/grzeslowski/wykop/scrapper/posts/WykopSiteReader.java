package pl.grzeslowski.wykop.scrapper.posts;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
class WykopSiteReader implements SiteReader {
    private static final Pattern URL_TO_ID_PATTERN = Pattern.compile(".*wykop\\.pl/link/([0-9]+).*");
    private final PostReader postReader;
    private final DateParser dateParser;

    @Autowired
    public WykopSiteReader(PostReader postReader, DateParser dateParser) {
        this.postReader = Preconditions.checkNotNull(postReader);
        this.dateParser = Preconditions.checkNotNull(dateParser);
    }

    @Override
    public Site parse(Html html) {
        return new Site(
                parseId(html),
                parseHot(html),
                parseScore(html),
                parseAdded(html),
                parseAuthor(html),
                parseContent(html),
                postReader.readPosts(html).collect(Collectors.toList())
        );
    }

    private Id parseId(Html html) {
        final String url = html.select("meta")
                .filter(e -> e.hasAttribute("property"))
                .filter(e -> "og:url".equals(e.attribute("property")))
                .map(e -> e.attribute("content"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find URL for ID"));
        final Matcher matcher = URL_TO_ID_PATTERN.matcher(url);
        if (matcher.matches()) {
            final int id = Integer.parseInt(matcher.group(1));
            return new Id(id);
        } else {
            throw new IllegalArgumentException("Cannot find ID in URL " + url);
        }
    }

    private boolean parseHot(Html html) {
        return html.select("div.diggbox")
                .flatMap(e -> e.select("i.icon.hot"))
                .findFirst()
                .isPresent();
    }

    private String parseAuthor(Html html) {
        return html.select("div.usercard b")
                .map(Html.Element::text)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find author!"));
    }

    private Date parseAdded(Html html) {
        return html.select("div.space.information.bdivider div p b time")
                .map(e -> e.attribute("datetime"))
                .map(dateParser::parse)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot parse date"));
    }

    private Score parseScore(Html html) {
        String upVotes = html.select("tr.infopanel a")
                .filter(e -> "#voters".equals(e.attribute("href")))
                .flatMap(e -> e.select("b"))
                .map(Html.Element::text)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find up votes"));

        String downVotes = html.select("tr.infopanel a")
                .filter(e -> "#votersBury".equals(e.attribute("href")))
                .flatMap(e -> e.select("b"))
                .map(Html.Element::text)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find up votes"));

        return new Score(
                Integer.parseInt(upVotes),
                Integer.parseInt(downVotes)
        );
    }

    private String parseContent(Html html) {
        return html.select("body div#site div.article p.text")
                .map(Html.Element::text)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find content"));
    }
}
