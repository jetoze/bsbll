package bsbll.team;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Team {
    private final TeamId id;
    private final TeamName name;
    
    public Team(TeamId id, TeamName name) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
    }

    public TeamId getId() {
        return id;
    }
    
    public TeamName getName() {
        return name;
    }
    
    public String getAbbreviation() {
        return name.getAbbreviation();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", name.getFullName(), id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) ||
                ((obj instanceof Team) && this.id.equals(((Team) obj).id));
    }
}
