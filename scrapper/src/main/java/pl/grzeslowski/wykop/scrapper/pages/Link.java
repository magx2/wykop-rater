package pl.grzeslowski.wykop.scrapper.pages;

import static com.google.common.base.Preconditions.checkNotNull;

public class Link {
    private final String link;

    public Link(String link) {
        this.link = checkNotNull(link);
    }

    public String getLink() {
        return link;
    }
}
