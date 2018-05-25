package bsbll.league;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import bsbll.stats.Average;
import bsbll.team.GamesBehind;
import bsbll.team.Record;
import bsbll.team.Team;
import bsbll.team.WLT;
import tzeth.collections.ImCollectors;

public final class Standings {
    private final ImmutableMap<Team, Entry> entries;
    
    public static Standings of(Map<Team, Record> teamRecords) {
        WLT gbLeader = teamRecords.values().stream()
                .max(Comparator.comparingInt(r -> r.getWins() - r.getLosses()))
                .map(Record::getWlt)
                .orElseThrow(AssertionError::new);
        
        Function<Map.Entry<Team, Record>, Standings.Entry> entryFactory = e -> {
            GamesBehind gb = e.getValue().getWlt().gamesBehind(gbLeader);
            return new Standings.Entry(e.getKey(), e.getValue(), gb);
        };
        ImmutableMap<Team, Entry> entries = teamRecords.entrySet().stream()
                .map(entryFactory)
                .collect(ImCollectors.toMap(Entry::getTeam, e -> e));
        return new Standings(entries);
    }

    private Standings(ImmutableMap<Team, Entry> teams) {
        this.entries = requireNonNull(teams);
    }

    /**
     * Returns the standings ordered by winning percentage.
     */
    public ImmutableList<Entry> list() {
        Comparator<Entry> order = Comparator.comparing(Entry::getWinPct).reversed();
        // TODO: Implement and document tie-breakers.
        return list(order);
    }

    /**
     * Returns the standings ordered by the given strategy.
     */
    public ImmutableList<Entry> list(Comparator<? super Entry> order) {
        return entries.values().stream()
                .sorted(order)
                .collect(ImCollectors.toList());
    }
    
    public Record getRecord(Team team) {
        Entry e = entries.get(requireNonNull(team));
        checkArgument(e != null, "No such team in these Standings: " + team);
        return e.getRecord();
    }
    
    public ImmutableList<Team> sortByWinPct() {
        Comparator<Team> order = Comparator.comparing((Team t) -> getRecord(t).getWinPct()).reversed();
        return entries.keySet().stream()
                .sorted(order)
                .collect(ImCollectors.toList());
    }
    
    
    public static final class Entry {
        private final Team team;
        private final Record record;
        private final GamesBehind gb;
        
        public Entry(Team team) {
            this(team, new Record(), new GamesBehind(0));
        }
        
        public Entry(Team team, Record record, GamesBehind gb) {
            this.team = requireNonNull(team);
            this.record = requireNonNull(record);
            this.gb = requireNonNull(gb);
        }

        public Team getTeam() {
            return team;
        }

        public Record getRecord() {
            return record;
        }
        
        public Average getWinPct() {
            return record.getWinPct();
        }

        public GamesBehind getGamesBehind() {
            return gb;
        }
    }
    
}
