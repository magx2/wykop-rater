package pl.grzeslowski.wykop.scrapper.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.scrapper.posts.Id;
import pl.grzeslowski.wykop.scrapper.posts.Site;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import static java.lang.String.format;

@Service
class JsonCrud implements SiteCrud{
    private static final Logger log = LoggerFactory.getLogger(JsonCrud.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final File basePath;

    @Autowired
    public JsonCrud(
            @Value("${fileCrud.basePath}") File basePath) {
        Preconditions.checkArgument(basePath.exists(), "basePath == " + basePath.getAbsolutePath());
        Preconditions.checkArgument(basePath.isDirectory(), "basePath == " + basePath.getAbsolutePath());
        this.basePath = basePath;
    }

    @Override
    public void save(Site site) {
        final File toSave = createFileToSave(site.getId());
        try {
            log.info("Saving to file {}", toSave.getAbsolutePath());
            mapper.writeValue(toSave, site);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Problem with saving to file %s!", toSave.getAbsolutePath()), e);
        }
    }

    private File createFileToSave(Id id) {
        return new File(basePath.getAbsolutePath() + File.separator + createFileName(id));
    }

    private String createFileName(Id id) {
        return format("site-%s.json", id.getId());
    }

    @Override
    public boolean exists(Id id) {
        return createFileToSave(id).exists();
    }
}
