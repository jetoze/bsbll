package bsbll.team;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bsbll.player.Player;
import bsbll.player.PlayerId;

public final class TeamBuilder {
    private final TeamId id;
    private String mainName;
    private String nickname;
    private String abbrev;
    private final List<Player> batters = new ArrayList<>();
    private final List<Player> pitchers = new ArrayList<>();

    public TeamBuilder(TeamId id) {
        this.id = requireNonNull(id);
    }
    
    public static TeamBuilder newBuilder(TeamId id) {
        return new TeamBuilder(id);
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
    
    public TeamBuilder withBatter(String id) {
        batters.add(getPlayer(id));
        return this;
    }
    
    public TeamBuilder withBatters(String... ids) {
        Arrays.stream(ids).forEach(this::withBatter);
        return this;
    }

    private Player getPlayer(String id) {
        // TODO: Once we have it, lookup player data (such as name) from Lahman.
        return new Player(PlayerId.of(id));
    }
    
    public TeamBuilder withPitcher(String id) {
        pitchers.add(getPlayer(id));
        return this;
    }
    
    public TeamBuilder withPitchers(String... ids) {
        Arrays.stream(ids).forEach(this::withPitcher);
        return this;
    }
    
    public Team build() {
        TeamName name = new TeamName(mainName, nickname, abbrev);
        Roster roster = new Roster(batters, pitchers);
        return new Team(id, name, roster);
    }
}
