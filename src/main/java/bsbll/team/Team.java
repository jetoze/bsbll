package bsbll.team;

import static java.util.Objects.requireNonNull;

public final class Team {
    private final TeamId id;
    private final TeamName name;
    private final Roster roster;
    
    // TODO: Is a "Team" tied to a given year? If so it needs a Roster.
    // If not tied to a given year, we need some abstraction that associates a 
    // Team with its Roster. What would that abstraction be called?
    
    public Team(TeamId id, TeamName name, Roster roster) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.roster = requireNonNull(roster);
    }

    public TeamId getId() {
        return id;
    }
    
    public TeamName getName() {
        return name;
    }
    
    public Roster getRoster() {
        return roster;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", name.getFullName(), id);
    }
    
}
