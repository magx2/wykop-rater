package pl.grzeslowski.wykop.scrapper.pages;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.grzeslowski.wykop.scrapper.ScrapperApplicationTestContext;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScrapperApplicationTestContext.class)
public class WykopLinkIteratorTest {
    @Autowired
    private WykopPageIterator wykopPageIterator;

    @Test
    public void shouldFindSecondPage() {

        // given
        Link startLink = new Link("http://www.wykop.pl/");

        // when
        final Link secondLink = wykopPageIterator.next(startLink);

        // then
        assertThat(secondLink.getLink()).isEqualTo("http://www.wykop.pl/strona/2/");
    }

    @Test
    public void shouldFindNextPage() {

        // given
        Link startLink = new Link("http://www.wykop.pl/strona/5");

        // when
        final Link secondLink = wykopPageIterator.next(startLink);

        // then
        assertThat(secondLink.getLink()).isEqualTo("http://www.wykop.pl/strona/6/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionBecauseCannotParseLink() {

        // given
        Link startLink = new Link("http://www.wykop.pl/stroa/5");

        // when
        wykopPageIterator.next(startLink);

        // then
    }
}
