package pl.grzeslowski.wykop.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class Post {
    private final String author;
    private final Date publishTime;
    private final Score score;
    private final String content;
    private final List<Post> responses;

    public Post(@JsonProperty("author") String author, @JsonProperty("publishTime") Date publishTime, @JsonProperty("score") Score score,
                @JsonProperty("content")  String content, @JsonProperty("responses") List<Post> responses) {
        this.author = Preconditions.checkNotNull(author);
        this.publishTime = Preconditions.checkNotNull(publishTime);
        this.score = Preconditions.checkNotNull(score);
        this.content = Preconditions.checkNotNull(content);
        this.responses = Preconditions.checkNotNull(responses);
    }

    public Post(String author, Date publishTime, Score score, String content) {
        this(author, publishTime, score, content, Collections.emptyList());
    }

    public String getAuthor() {
        return author;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public Score getScore() {
        return score;
    }

    public String getContent() {
        return content;
    }

    public List<Post> getResponses() {
        return responses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        if (!author.equals(post.author)) return false;
        if (!publishTime.equals(post.publishTime)) return false;
        if (!score.equals(post.score)) return false;
        return content.equals(post.content);
    }

    @Override
    public int hashCode() {
        int result = author.hashCode();
        result = 31 * result + publishTime.hashCode();
        result = 31 * result + score.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "author='" + author + '\'' +
                ", publishTime=" + publishTime +
                ", content='" + content + '\'' +
                '}';
    }
}
