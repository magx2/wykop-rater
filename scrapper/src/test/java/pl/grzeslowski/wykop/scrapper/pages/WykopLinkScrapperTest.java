package pl.grzeslowski.wykop.scrapper.pages;

import com.google.common.base.Preconditions;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WykopLinkScrapperTest {
    @Autowired
    private WykopLinkScrapper wykopLinkScrapper;

    @Test
    public void shouldScrapData() throws IOException {
        // given
        final Html html = new Html(Jsoup.parse(pageToScrap(), "UTF-8"));

        // when
        final List<Link> links = wykopLinkScrapper.scrapLinks(html).collect(toList());

        // then
        assertThat(links).hasSize(59); // niby ma byc 54 ale jakos jest 59...
    }

    @SuppressWarnings("ConstantConditions")
    private File pageToScrap() {
        final String path = this.getClass()
                .getClassLoader()
                .getResource("WykopMainPageToScrapLinks.html")
                .getFile();
        final File file = new File(path);
        Preconditions.checkArgument(file.exists());
        return file;
    }
}
