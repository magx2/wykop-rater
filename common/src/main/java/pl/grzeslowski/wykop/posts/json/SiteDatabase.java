package pl.grzeslowski.wykop.posts.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import pl.grzeslowski.wykop.posts.Id;
import pl.grzeslowski.wykop.posts.Site;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import static java.lang.String.format;

public class SiteDatabase {
    private static final String EXTENSION = ".json";
    private final ObjectMapper mapper = new ObjectMapper();

    public void write(File directory, Site site) {
        Preconditions.checkArgument(directory.exists());
        Preconditions.checkArgument(directory.isDirectory());
        final File toSave = createFileToSave(directory, site.getId());
        try {
            mapper.writeValue(toSave, site);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Problem with saving to file %s!", toSave.getAbsolutePath()), e);
        }
    }

    public File createFileToSave(File directory, Id id) {
        return new File(directory.getAbsolutePath() + File.separator + createFileName(id));
    }

    private String createFileName(Id id) {
        return format("site-%s" + EXTENSION, id.getId());
    }

    public Site read(File file) {
        Preconditions.checkArgument(file.exists());
        Preconditions.checkArgument(file.isFile());
        Preconditions.checkArgument(file.getAbsolutePath().endsWith(EXTENSION), "file.path = " + file.getAbsolutePath());
        try {
            return mapper.readValue(file, Site.class);
        } catch (IOException e) {
            throw new UncheckedIOException(format("Problem with reading from file %s!", file.getAbsolutePath()), e);
        }
    }
}
