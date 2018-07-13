package bsbll.team;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

public final class TeamBuilder {
    private final TeamId id;
    private String mainName;
    private String nickname;
    private String abbrev;

    public TeamBuilder(TeamId id) {
        this.id = requireNonNull(id);
    }
    
    public static TeamBuilder newBuilder(TeamId id) {
        return newBuilder(id);
    }

    public TeamBuilder withMainName(String name) {
        this.mainName = checkNotEmpty(name);
        return this;
    }
    
    public TeamBuilder withNickname(String name) {
        this.nickname = checkNotEmpty(name);
        return this;
    }
    
    public TeamBuilder withAbbreviation(String abbrev) {
        this.abbrev = checkNotEmpty(abbrev);
        return this;
    }
    
    public Team build() {
        TeamName name = new TeamName(mainName, nickname, abbrev);
        return new Team(id, name);
    }
}
