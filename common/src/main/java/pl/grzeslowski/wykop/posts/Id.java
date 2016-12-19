package pl.grzeslowski.wykop.posts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Id {
    private final long id;

    public Id(@JsonProperty("id") long id) {
        Preconditions.checkArgument(id > 0, "ID == " + id);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
