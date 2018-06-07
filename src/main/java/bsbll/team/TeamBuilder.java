package bsbll.team;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bsbll.player.Player;
import bsbll.player.PlayerFactory;

public final class TeamBuilder {
    private final TeamId id;
    private String mainName;
    private String nickname;
    private String abbrev;
    private final PlayerFactory playerFactory;
    private final List<Player> batters = new ArrayList<>();
    private final List<Player> pitchers = new ArrayList<>();

    public TeamBuilder(TeamId id, PlayerFactory playerFactory) {
        this.id = requireNonNull(id);
        this.playerFactory = requireNonNull(playerFactory);
    }
    
    public static TeamBuilder newBuilder(TeamId id) {
        return newBuilder(id, PlayerFactory.defaultFactory());
    }
    
    public static TeamBuilder newBuilder(TeamId id, PlayerFactory playerFactory) {
        return new TeamBuilder(id, playerFactory);
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
        requireNonNull(id);
        return playerFactory.getPlayer(id);
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
