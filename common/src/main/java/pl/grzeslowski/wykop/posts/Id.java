package pl.grzeslowski.wykop.posts;

import com.google.common.base.Preconditions;

public class Id {
    private final long id;

    public Id(long id) {
        Preconditions.checkArgument(id > 0, "ID == " + id);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
