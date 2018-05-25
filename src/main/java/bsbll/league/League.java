package bsbll.league;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.game.LineScore;
import bsbll.team.Record;
import bsbll.team.RunDifferential;
import bsbll.team.Team;
import bsbll.team.TeamId;
import tzeth.collections.ImCollectors;

@NotThreadSafe
public final class League {
    private final LeagueId id;
    private final ImmutableMap<TeamId, Team> teams;
    private final Map<Team, Record> teamRecords;
    private final List<LineScore> gameLog = new ArrayList<>();
    
    public League(LeagueId id, Team... teams) {
        this(id, Arrays.asList(teams));
    }
    
    public League(LeagueId id, Collection<Team> teams) {
        this.id = requireNonNull(id);
        this.teams = teams.stream()
                .collect(ImCollectors.toMap(Team::getId, t -> t));
        checkArgument(teams.size() >= 2, "Must provide at least two teams");
        this.teamRecords = teams.stream()
                .collect(Collectors.toMap(t -> t, t -> new Record()));
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
    
    public Record getRecord(Team team) {
        Record r = teamRecords.get(requireNonNull(team));
        checkArgument(r != null, "No such team in this league: " + team);
        return r;
    }

    public Standings getStandings() {
        return Standings.of(teamRecords);
    }
    
    public void addGame(LineScore score) {
        addGames(score);
    }
    
    public void addGames(LineScore... scores) {
        addGames(Arrays.asList(scores));
    }
    
    public void addGames(Collection<LineScore> scores) {
        scores.forEach(this::updateTeamRecords);
        this.gameLog.addAll(scores);
    }
    
    private void updateTeamRecords(LineScore score) {
        Team homeTeam = score.getHomeTeam();
        Record homeRecord = teamRecords.get(homeTeam);
        checkArgument(homeRecord != null, "No such team: " + homeTeam);
        Team visitingTeam = score.getVisitingTeam();
        Record visitingRecord = teamRecords.get(visitingTeam);
        checkArgument(visitingRecord != null, "No such team: " + visitingTeam);
        
        RunDifferential homeTeamRunDiff = score.getHomeTeamRunDifferential();
        teamRecords.put(homeTeam, homeRecord.addGame(homeTeamRunDiff));
        teamRecords.put(visitingTeam, visitingRecord.addGame(homeTeamRunDiff.reverse()));

    }
    
    public ImmutableList<LineScore> getGameLog() {
        return ImmutableList.copyOf(gameLog);
    }
}
