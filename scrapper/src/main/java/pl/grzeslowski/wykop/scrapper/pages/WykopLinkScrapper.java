package pl.grzeslowski.wykop.scrapper.pages;

import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.util.stream.Stream;

@Service
class WykopLinkScrapper implements LinkScrapper{
    @Override
    public Stream<Link> scrapLinks(Html html) {
        return html.select("h2 a")
                .filter(e -> !e.hasAttribute("rel"))
                .map(e -> new Link(e.attribute("href"), e.text()));
    }
}
