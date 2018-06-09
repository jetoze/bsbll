package bsbll.team;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import tzeth.preconds.MorePreconditions;

@Immutable
public final class TeamId {
    private final String id;
    
    public TeamId(String id) {
        this.id = MorePreconditions.checkNotEmpty(id);
    }

    public static TeamId of(String id) {
        return new TeamId(id);
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
