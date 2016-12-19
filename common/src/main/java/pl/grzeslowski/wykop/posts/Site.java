package pl.grzeslowski.wykop.posts;

import com.google.common.base.Preconditions;

import java.util.Date;
import java.util.List;

public class Site {
    private final Id id;
    private final boolean hot;
    private final Score wykop;
    private final Date added;
    private final String author;
    private final String content;
    private final List<Post> posts;

    public Site(Id id, boolean hot, Score wykop, Date added, String author, String content,
                List<Post> posts) {
        this.id = Preconditions.checkNotNull(id);
        this.hot = hot;
        this.wykop = Preconditions.checkNotNull(wykop);
        this.added = Preconditions.checkNotNull(added);
        this.author = Preconditions.checkNotNull(author);
        this.content = Preconditions.checkNotNull(content);
        this.posts = Preconditions.checkNotNull(posts);
    }

    public Id getId() {
        return id;
    }

    public boolean isHot() {
        return hot;
    }

    public Score getWykop() {
        return wykop;
    }

    public Date getAdded() {
        return added;
    }

    public String getAuthor() {
        return author;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
