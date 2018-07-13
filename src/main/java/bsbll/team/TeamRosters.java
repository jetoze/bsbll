package bsbll.team;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class TeamRosters {
    private final ImmutableMap<TeamId, Roster> rosters;
    
    public TeamRosters(Map<TeamId, Roster> rosters) {
        this.rosters = ImmutableMap.copyOf(rosters);
    }
    
    public Roster get(Team team) {
        return get(team.getId());
    }
    
    public Roster get(TeamId id) {
        Roster r = rosters.get(requireNonNull(id));
        checkArgument(r != null, "Unknown team ID: " + id);
        return r;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    
    public static final class Builder {
        private final ImmutableMap.Builder<TeamId, Roster> mapBuilder = ImmutableMap.builder();
        
        public Builder add(Team team, Roster roster) {
            mapBuilder.put(team.getId(), roster);
            return this;
        }
        
        public TeamRosters build() {
            return new TeamRosters(mapBuilder.build());
        }
    }
}
