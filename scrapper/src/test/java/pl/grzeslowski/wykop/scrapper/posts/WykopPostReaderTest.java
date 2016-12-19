package pl.grzeslowski.wykop.scrapper.posts;

import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.grzeslowski.wykop.posts.Post;
import pl.grzeslowski.wykop.posts.Score;
import pl.grzeslowski.wykop.scrapper.ScrapperApplicationTestContext;
import pl.grzeslowski.wykop.scrapper.TestUtils;
import pl.grzeslowski.wykop.scrapper.html.Html;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScrapperApplicationTestContext.class)
public class WykopPostReaderTest {
    private final SimpleDateFormat postDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    @Autowired
    private WykopPostReader postReader;

    @Test
    public void shouldReadPosts() throws IOException, ParseException {

        // given
        final File postPage = TestUtils.loadFileFromClassLoader("WykopHtmlWithPosts.html");
        final Html html = new Html(Jsoup.parse(postPage, "UTF-8"));

        final Post response1 = new Post(
                "Kotlet_Rybny",
                postDate.parse("2016-12-16T16:19:52+01:00"),
                new Score(406, 12),
                "@SureBetyPL: Dla mnie to czysty krok w stronę cenzury na większą skalę. Będą mieli rejestr będą mogli blokować co chcą według swojego widzimisię i blokowana strona ma gówno do gadania. ACTA 2.0 jak dla mnie. Szkoda że nie ma takiego echa w internecie żeby wyprowadzić jak wtedy ludzi na ulice."
        );
        final Post response2 = new Post(
                "SureBetyPL",
                postDate.parse("2016-12-16T16:22:13+01:00"),
                new Score(30,5 ),
                "@Kotlet_Rybny: Obawiali się tego pewnie i nazwali przez to ten rejestr \"Rejestrem stron niezgodnych z ustawą\". Zataguj to również #poker jak możesz."
        );
        final Post response3 = new Post(
                "tolstyy00",
                postDate.parse("2016-12-16T16:43:59+01:00"),
                new Score(121, 6),
                "@SureBetyPL w ogóle uznawanie pokera za grę hazardowa to kpina, jak i same nowe regulacje. Ciemnogród."
        );

        final Post post1 = new Post(
                "SureBetyPL",
                postDate.parse("2016-12-16T16:18:17+01:00"),
                new Score(449, 13),
                "Warto dodać, że operatorzy telekomunikacyjni będą musieli za to blokowanie stron zapłacić - czyli podziękujcie PiS za podwyżkę abonamentu za internet.",
                Arrays.asList(response1, response2, response3)
        );

        // when
        final List<Post> posts = postReader.readPosts(html).collect(toList());

        // then
        assertThat(posts).contains(post1);
        final List<Post> responses = posts.get(0).getResponses();
        assertThat(responses.get(0)).isEqualTo(response1);
        assertThat(responses.get(1)).isEqualTo(response2);
        assertThat(responses.get(2)).isEqualTo(response3);
    }
}
