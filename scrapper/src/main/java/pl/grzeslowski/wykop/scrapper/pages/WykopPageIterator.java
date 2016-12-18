package pl.grzeslowski.wykop.scrapper.pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
class WykopPageIterator implements PageIterator {
    private static final Logger log = LoggerFactory.getLogger(WykopPageIterator.class);
    private static final Pattern MAIN_PAGE_PATTERN = Pattern.compile("(?:http://)?www\\.wykop\\.pl(?:/)?");
    private static final Pattern PAGE_PATTERN = Pattern.compile("(?:http://)?www\\.wykop\\.pl/strona/([0-9]+)(?:/)?");

    @Override
    public Link next(Link link) {
        final String linkVal = link.getLink();
        if (isMainPage(linkVal)) {
            log.info("Going to page nr 2");
            return createNewPage(2);
        } else {
            final Matcher matcher = PAGE_PATTERN.matcher(linkVal);
            if (matcher.matches()) {
                final int pageNumber = Integer.parseInt(matcher.group(1));
                final int nextPageNumber1 = pageNumber + 1;
                log.info("Going to page nr {}", nextPageNumber1);
                return createNewPage(nextPageNumber1);
            } else {
                throw new IllegalArgumentException(format("Cannot parse this linkVal: %s!", linkVal));
            }
        }
    }

    private boolean isMainPage(String link) {
        return MAIN_PAGE_PATTERN.matcher(link).matches();
    }

    private Link createNewPage(int pageNumber) {
        return new Link(format("http://www.wykop.pl/strona/%s/", pageNumber));
    }

}
