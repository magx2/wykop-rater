package pl.grzeslowski.wykop.scrapper.pages;

import static com.google.common.base.Preconditions.checkNotNull;

public class Link {
    private final String link;
    private final String text;

    public Link(String link) {
        this(link, "");
    }

    public Link(String link, String text) {
        this.link = checkNotNull(link);
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }
}
