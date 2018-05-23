package bsbll.team;

import javax.annotation.Nullable;

import bsbll.player.PlayerId;
import tzeth.preconds.MorePreconditions;

public final class TeamId {
    private final String id;
    
    public TeamId(String id) {
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
                ((obj instanceof TeamId) && (this.id.equals(((TeamId) obj).id)));
    }

    @Override
    public String toString() {
        return id;
    }
}
