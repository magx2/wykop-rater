package pl.grzeslowski.wykop.scrapper.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.pages.Link;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Service
class JsoupHtmlDowloader implements HtmlDownloader{
    private static final Logger log = LoggerFactory.getLogger(JsoupHtmlDowloader.class);

    @Value("${jsoup.waitAfterSocketTimeoutException}")
    private int waitAfterSocketTimeoutException;

    @Override
    public Html download(Link link) {
        try {
            log.info("Downloading HTML for {}", link.getLink());
            final Document document = Jsoup.connect(link.getLink()).get();
            return new Html(document);
        } catch (SocketTimeoutException e) {
            log.warn("Got SocketTimeoutException({}), will wait for {} secs", e.getMessage(), waitAfterSocketTimeoutException);
            try {
                TimeUnit.SECONDS.sleep(waitAfterSocketTimeoutException);
            } catch (InterruptedException e1) {
                // ignore
            }
            return download(link);
        } catch (IOException e) {
            throw new RuntimeException(format("Couldn't download HTML from %s", link), e);
        }
    }
}
