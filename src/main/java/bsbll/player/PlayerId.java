package bsbll.player;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import tzeth.preconds.MorePreconditions;

@Immutable
public final class PlayerId {
    private final String id;
    
    public PlayerId(String id) {
        this.id = MorePreconditions.checkNotEmpty(id);
    }

    public static PlayerId of(String id) {
        return new PlayerId(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) ||
                ((obj instanceof PlayerId) && (this.id.equals(((PlayerId) obj).id)));
    }

    @Override
    public String toString() {
        return id;
    }
}
