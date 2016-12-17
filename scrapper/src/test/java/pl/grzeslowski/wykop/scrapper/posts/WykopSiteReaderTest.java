package pl.grzeslowski.wykop.scrapper.posts;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.grzeslowski.wykop.scrapper.TestUtils;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WykopSiteReaderTest {
    private final SimpleDateFormat postDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    @Autowired
    private WykopSiteReader siteReader;

    @Test
    public void shouldParseWykopSite() throws IOException, ParseException {

        // given
        final File postPage = TestUtils.loadFileFromClassLoader("WykopHtmlWithPosts.html");
        final Html html = new Html(Jsoup.parse(postPage, "UTF-8"));

        // when
        final Site site = siteReader.parse(html);

        // then
        assertThat(site.getId().getId()).isEqualTo(3507065);
        assertThat(site.isHot()).isTrue();
        assertThat(site.getAdded()).isEqualTo(postDate.parse("2016-12-16T16:13:21+01:00"));
        assertThat(site.getAuthor()).isEqualTo("Kotlet_Rybny");
        assertThat(site.getPosts()).isNotEmpty();

        final Score wykop = site.getWykop();
        assertThat(wykop.getUpVotes()).isEqualTo(2732);
        assertThat(wykop.getDownVotes()).isEqualTo(31);
    }
}
