package pl.grzeslowski.wykop.scrapper.pages;

import pl.grzeslowski.wykop.scrapper.html.Html;

import java.util.stream.Stream;

public interface LinkScrapper {
    Stream<Link> scrapLinks(Html html);
}
