package pl.grzeslowski.wykop.scrapper.posts;

import pl.grzeslowski.wykop.scrapper.html.Html;

public interface SiteReader {
    Site parse(Html html);
}
