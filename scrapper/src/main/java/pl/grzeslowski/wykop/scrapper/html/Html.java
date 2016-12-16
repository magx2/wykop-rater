package pl.grzeslowski.wykop.scrapper.html;

import org.jsoup.nodes.Document;

import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class Html {
    private final Document document;

    public Html(Document document) {
        this.document = checkNotNull(document);
    }


    public Stream<Element> select(String selector) {
        checkNotNull(selector);
        return document.select(selector)
                .stream()
                .map(Element::new);
    }

    public static class Element {
        private final org.jsoup.nodes.Element element;

        Element(org.jsoup.nodes.Element element) {
            this.element = checkNotNull(element);
        }

        public String attribute(String attribute) {
            checkNotNull(attribute);
            return element.attr(attribute);
        }

        public String text() {
            return element.text();
        }

        public boolean hasAttribute(String attribute) {
            return element.hasAttr(attribute);
        }

        public Stream<Element> select(String cssSelector) {
            return element.select(cssSelector)
                    .stream()
                    .map(Element::new);
        }
    }
}
