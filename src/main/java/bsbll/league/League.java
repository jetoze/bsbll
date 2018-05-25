package bsbll.league;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.game.LineScore;
import bsbll.team.Team;
import bsbll.team.TeamId;
import tzeth.collections.ImCollectors;

@NotThreadSafe
public final class League {
    private final LeagueId id;
    private final ImmutableMap<TeamId, Team> teams;
    // TODO: Only store Team records, and generate a new Standings on demand?
    // In fact, we could store only the game log, and generate Team records
    // and standings on demand.
    private Standings standings;
    private final List<LineScore> gameLog = new ArrayList<>();
    
    public League(LeagueId id, Team... teams) {
        this(id, Arrays.asList(teams));
    }
    
    public League(LeagueId id, Collection<Team> teams) {
        this.id = requireNonNull(id);
        this.teams = teams.stream()
                .collect(ImCollectors.toMap(Team::getId, t -> t));
        checkArgument(teams.size() >= 2, "Must provide at least two teams");
        this.standings = Standings.initialize(teams);
    }

    public LeagueId getId() {
        return id;
    }
    
    public ImmutableList<Team> getTeams() {
        // TODO: Store the teams in alphabetic order, so that we don't have to
        // sort them every time this method is called.
        return teams.values().stream()
                .sorted(Comparator.comparing(t -> t.getName().getMainName()))
                .collect(ImCollectors.toList());
    }
    
    public Standings getStandings() {
        return standings;
    }
    
    public void addGame(LineScore score) {
        addGames(score);
    }
    
    public void addGames(LineScore... scores) {
        addGames(Arrays.asList(scores));
    }
    
    public void addGames(Collection<LineScore> scores) {
        this.gameLog.addAll(scores);
        this.standings = standings.addGames(scores);
    }
    
    public ImmutableList<LineScore> getGameLog() {
        return ImmutableList.copyOf(gameLog);
    }
}
