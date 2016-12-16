package pl.grzeslowski.wykop.scrapper.html;

import pl.grzeslowski.wykop.scrapper.pages.Link;

public interface HtmlDownloader {
    Html download(Link link);
}
