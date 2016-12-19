package pl.grzeslowski.wykop.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public Site(@JsonProperty("id") Id id, @JsonProperty("hot") boolean hot, @JsonProperty("wykop") Score wykop,
                @JsonProperty("added") Date added, @JsonProperty("author") String author, @JsonProperty("content") String content,
                @JsonProperty("posts") List<Post> posts) {
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

    public String getContent() {
        return content;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
