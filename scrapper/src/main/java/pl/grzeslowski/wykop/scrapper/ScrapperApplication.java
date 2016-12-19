package pl.grzeslowski.wykop.scrapper;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import pl.grzeslowski.wykop.posts.Site;
import pl.grzeslowski.wykop.scrapper.html.Html;
import pl.grzeslowski.wykop.scrapper.html.HtmlDownloader;
import pl.grzeslowski.wykop.scrapper.io.SiteCrud;
import pl.grzeslowski.wykop.scrapper.pages.Link;
import pl.grzeslowski.wykop.scrapper.pages.LinkScrapper;
import pl.grzeslowski.wykop.scrapper.pages.PageIterator;
import pl.grzeslowski.wykop.scrapper.posts.SiteReader;

import java.util.Optional;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(basePackageClasses = ScrapperApplication.class)
public class ScrapperApplication implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(ScrapperApplication.class);

	@Value("#{new pl.grzeslowski.wykop.scrapper.pages.Link(\"${pageToStart}\")}")
	private Link pageToStart;

	private final PageIterator pageIterator;
	private final HtmlDownloader htmlDownloader;
	private final LinkScrapper linkScrapper;
	private final SiteReader siteReader;
	private final SiteCrud siteCrud;

	@Autowired
	public ScrapperApplication(PageIterator pageIterator, HtmlDownloader htmlDownloader, LinkScrapper linkScrapper, SiteReader siteReader, SiteCrud siteCrud) {
		this.pageIterator = Preconditions.checkNotNull(pageIterator);
		this.htmlDownloader = Preconditions.checkNotNull(htmlDownloader);
		this.linkScrapper = Preconditions.checkNotNull(linkScrapper);
		this.siteReader = Preconditions.checkNotNull(siteReader);
		this.siteCrud = Preconditions.checkNotNull(siteCrud);
	}

	public static void main(String[] args) {
		SpringApplication.run(ScrapperApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.iterate(pageToStart, pageIterator::next)
				.map(htmlDownloader::download)
				.flatMap(linkScrapper::scrapLinks)
				.map(htmlDownloader::download)
				.map(this::parseSite)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(site -> !siteCrud.exists(site))
				.forEach(siteCrud::save);
	}

	/**
	 * Look at test {@link pl.grzeslowski.wykop.scrapper.posts.WykopSiteReaderTest} (in test).
	 *
	 * @param siteHtml
	 * @return
	 */
	private Optional<Site> parseSite(Html siteHtml) {
		try {
			return java.util.Optional.of(siteReader.parse(siteHtml));
		} catch (IllegalArgumentException e) {
			log.warn("Got error! Message: {}", e.getMessage());
			return java.util.Optional.empty();
		}
	}
}
