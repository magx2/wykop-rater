package pl.grzeslowski.wykop.scrapper.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.pages.Link;

import java.io.IOException;

import static java.lang.String.format;

@Service
class JsoupHtmlDowloader implements HtmlDownloader{
    private static final Logger log = LoggerFactory.getLogger(JsoupHtmlDowloader.class);

    @Override
    public Html download(Link link) {
        try {
            log.debug("Downloading HTML for {}.", link);
            final Document document = Jsoup.connect(link.getLink()).get();
            return new Html(document);
        } catch (IOException e) {
            throw new RuntimeException(format("Couldn't download HTML from %s", link), e);
        }
    }
}
