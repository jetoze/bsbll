package bsbll.league;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.game.LineScore;
import bsbll.team.Record;
import bsbll.team.RunDifferential;
import bsbll.team.Team;
import tzeth.collections.ImCollectors;

public final class Standings {
    private final ImmutableMap<Team, Record> teams;
    
    public Standings(Team... teams) {
        this(Arrays.asList(teams));
    }
    
    public Standings(Collection<Team> teams) {
        this.teams = teams.stream()
                .collect(ImCollectors.toMap(t -> t, t -> new Record()));
        checkArgument(this.teams.size() >= 2, "Must provide at least two teams");
    }
    
    private Standings(Map<Team, Record> teams) {
        this.teams = ImmutableMap.copyOf(teams);
    }
    
    public Standings addGame(LineScore score) {
        Team homeTeam = score.getHomeTeam();
        checkArgument(teams.containsKey(homeTeam));
        Team visitingTeam = score.getVisitingTeam();
        checkArgument(teams.containsKey(visitingTeam));
        Map<Team, Record> m = new HashMap<>(teams);
        RunDifferential homeTeamRunDiff = score.getHomeTeamRunDifferential();
        m.computeIfPresent(homeTeam, (t, r) -> r.addGame(homeTeamRunDiff));
        m.computeIfPresent(visitingTeam, (t, r) -> r.addGame(homeTeamRunDiff.reverse()));
        return new Standings(m);
    }
    
    public Record getRecord(Team team) {
        Record r = teams.get(requireNonNull(team));
        checkArgument(r != null, "No such team in these Standings: " + team);
        return r;
    }

    public ImmutableList<Map.Entry<Team, Record>> sortByWinPct2() {
        Comparator<Map.Entry<Team, Record>> order = Comparator.comparing(e -> e.getValue().getWinPct());
        order = order.reversed();
        return teams.entrySet().stream()
                .sorted(order)
                .collect(ImCollectors.toList());
    }
    
    public ImmutableList<Team> sortByWinPct() {
        Comparator<Team> order = Comparator.comparing((Team t) -> getRecord(t).getWinPct()).reversed();
        return teams.keySet().stream()
                .sorted(order)
                .collect(ImCollectors.toList());
                
    }
}
