package pl.grzeslowski.wykop.scrapper.io;

import pl.grzeslowski.wykop.scrapper.posts.Id;
import pl.grzeslowski.wykop.scrapper.posts.Site;

public interface SiteCrud {
    void save(Site site);

    boolean exists(Id id);

    default boolean exists(Site site) {
        return exists(site.getId());
    }
}
