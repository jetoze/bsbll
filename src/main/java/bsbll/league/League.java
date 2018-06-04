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
import java.util.stream.Stream;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.Year;
import bsbll.game.BoxScore;
import bsbll.game.GameResult;
import bsbll.game.LineScore;
import bsbll.player.Player;
import bsbll.player.PlayerId;
import bsbll.stats.BattingLeaders;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStatLine;
import bsbll.stats.PitchingStatLine;
import bsbll.team.Record;
import bsbll.team.RunDifferential;
import bsbll.team.Team;
import bsbll.team.TeamId;
import tzeth.collections.ImCollectors;

@NotThreadSafe
public final class League {
    private final LeagueId id;
    private final Year year;
    private final ImmutableMap<TeamId, Team> teams;
    private final Map<TeamId, Record> teamRecords;
    private final PlayerLeagueStats playerStats = new PlayerLeagueStats();
    private final List<BoxScore> boxScores = new ArrayList<>();
    private final List<GameResult> gameResults = new ArrayList<>();
    
    public League(LeagueId id, Year year, Team... teams) {
        this(id, year, Arrays.asList(teams));
    }
    
    public League(LeagueId id, Year year, Collection<Team> teams) {
        this.id = requireNonNull(id);
        this.year = requireNonNull(year);
        this.teams = teams.stream()
                .collect(ImCollectors.toMap(Team::getId, t -> t));
        checkArgument(teams.size() >= 2, "Must provide at least two teams");
        this.teamRecords = teams.stream()
                .collect(Collectors.toMap(Team::getId, t -> new Record()));
    }

    public LeagueId getId() {
        return id;
    }
    
    public Year getYear() {
        return year;
    }

    public ImmutableList<Team> getTeams() {
        // TODO: Store the teams in alphabetic order, so that we don't have to
        // sort them every time this method is called.
        return teams.values().stream()
                .sorted(Comparator.comparing(t -> t.getName().getMainName()))
                .collect(ImCollectors.toList());
    }
    
    public int getNumberOfTeams() {
        return teams.size();
    }
    
    public Team getTeam(TeamId id) {
        Team t = teams.get(id);
        checkArgument(t != null, "No such team in this league: " + id);
        return t;
    }
    
    public Record getRecord(Team team) {
        Record r = teamRecords.get(team.getId());
        checkArgument(r != null, "No such team in this league: " + team);
        return r;
    }

    public Standings getStandings() {
        Map<Team, Record> tmp = teamRecords.entrySet().stream()
                .collect(Collectors.toMap(e -> getTeam(e.getKey()), Map.Entry::getValue));
        return Standings.of(tmp);
    }
    
    public BattingStatLine getBattingStatLine(Player player) {
        return this.playerStats.getBattingStats(player);
    }
    
    public BattingStatLine getBattingStatLine(PlayerId playerId) {
        return this.playerStats.getBattingStats(playerId);
    }
    
    public PitchingStatLine getPitchingStatLine(Player player) {
        return this.playerStats.getPitchingStats(player);
    }
    
    public PitchingStatLine getPitchingStatLine(PlayerId playerId) {
        return this.playerStats.getPitchingStats(playerId);
    }
    
    public <T> BattingLeaders<T> getBattingLeaders(BattingStat<T> stat, int top) {
        return playerStats.getBattingLeaders(stat, top);
    }
    
    public <T> BattingLeaders<T> getBattingLeaders(BattingStat<T> stat, int top, int minAtBats) {
        return playerStats.getBattingLeaders(stat, top, minAtBats);
    }
    
    public void addBoxScores(BoxScore... boxScores) {
        addBoxScores(Arrays.asList(boxScores));
    }
    
    public void addBoxScores(Collection<BoxScore> boxScores) {
        addGameResults(boxScores.stream().map(BoxScore::toGameResult));
        boxScores.forEach(this.playerStats::update);
        this.boxScores.addAll(boxScores);
    }
    
    public void addGameResults(GameResult... results) {
        addGameResults(Arrays.asList(results));
    }
    
    public void addGameResults(Collection<GameResult> results) {
        addGameResults(results.stream());
    }
    
    public void addGameResults(Stream<GameResult> results) {
        results.forEach(this::addGameResultImpl);
    }
    
    private void addGameResultImpl(GameResult gr) {
        updateTeamRecords(gr);
        gameResults.add(gr);
    }

    public void updateTeamRecords(GameResult gr) {
        Team homeTeam = getTeam(gr.getHomeTeamId());
        Team visitingTeam = getTeam(gr.getVisitingTeamId());
        
        Record homeRecord = getRecord(homeTeam);
        Record visitingRecord = getRecord(visitingTeam);

        RunDifferential homeTeamRunDiff = new RunDifferential(gr.getHomeScore(), gr.getVisitingScore());
        teamRecords.put(homeTeam.getId(), homeRecord.addGame(homeTeamRunDiff));
        teamRecords.put(visitingTeam.getId(), visitingRecord.addGame(homeTeamRunDiff.reverse()));
    }
    
    public ImmutableList<LineScore> getGameLog() {
        return this.boxScores.stream()
                .map(BoxScore::getLineScore)
                .collect(ImCollectors.toList());
    }
}
