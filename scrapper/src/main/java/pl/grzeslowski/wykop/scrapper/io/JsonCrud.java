package pl.grzeslowski.wykop.scrapper.io;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.posts.Id;
import pl.grzeslowski.wykop.posts.Site;
import pl.grzeslowski.wykop.posts.json.SiteDatabase;

import java.io.File;

@Service
class JsonCrud implements SiteCrud{
    private static final Logger log = LoggerFactory.getLogger(JsonCrud.class);
    private final SiteDatabase siteDatabase = new SiteDatabase();
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
        siteDatabase.write(basePath, site);
    }

    @Override
    public boolean exists(Id id) {
        return siteDatabase.createFileToSave(basePath, id).exists();
    }
}
