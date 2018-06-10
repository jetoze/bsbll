package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.player.PlayerId;

@Immutable
public final class StatLeaders<T, S extends Stat<T>> {
    private final S stat;
    private final ImmutableList<Entry<T>> entries;
    
    public StatLeaders(S stat, List<Entry<T>> entries) {
        this.stat = requireNonNull(stat);
        this.entries = ImmutableList.copyOf(entries);
    }
    
    public static <T, S extends BattingStat<T>> StatLeaders<T, S> batting(Map<PlayerId, BattingStatLine> statLines,
            S stat, int top) {
        return batting(statLines, stat, top, i -> true);
    }
    
    public static <T, S extends BattingStat<T>> StatLeaders<T, S> batting(Map<PlayerId, BattingStatLine> statLines,
            S stat, int top, int minAtBats) {
        checkPositive(minAtBats);
        return batting(statLines, stat, top, e -> e.getValue().get(BattingStat.AT_BATS) >= minAtBats);
    }
    
    public static <T, S extends BattingStat<T>> StatLeaders<T, S> batting(Map<PlayerId, BattingStatLine> statLines,
            S stat, int top, Predicate<Map.Entry<PlayerId, BattingStatLine>> filter) {
        requireNonNull(stat);
        checkPositive(top);
        requireNonNull(filter);
        List<Entry<T>> entries = statLines.entrySet().stream()
                .filter(filter)
                .map(e -> new Entry<>(e.getKey(), e.getValue().get(stat)))
                .sorted(Comparator.comparing(Entry::getValue, stat.leaderOrder()))
                .limit(top)
                .collect(toList());
        return new StatLeaders<>(stat, entries);
    }
    
    public static <T, S extends PitchingStat<T>> StatLeaders<T, S> pitching(Map<PlayerId, PitchingStatLine> statLines,
            S stat, int top) {
        return pitching(statLines, stat, top, i -> true);
    }
    
    public static <T, S extends PitchingStat<T>> StatLeaders<T, S> pitching(Map<PlayerId, PitchingStatLine> statLines,
            S stat, int top, InningsPitched minIPs) {
        requireNonNull(minIPs);
        return pitching(statLines, stat, top, e -> e.getValue().get(PitchingStat.INNINGS_PITCHED).compareTo(minIPs) >= 0);
    }
    
    public static <T, S extends PitchingStat<T>> StatLeaders<T, S> pitching(Map<PlayerId, PitchingStatLine> statLines,
            S stat, int top, Predicate<Map.Entry<PlayerId, PitchingStatLine>> filter) {
        requireNonNull(stat);
        checkPositive(top);
        requireNonNull(filter);
        List<Entry<T>> entries = statLines.entrySet().stream()
                .filter(filter)
                .map(e -> new Entry<>(e.getKey(), e.getValue().get(stat)))
                .sorted(Comparator.comparing(Entry::getValue, stat.leaderOrder()))
                .limit(top)
                .collect(toList());
        return new StatLeaders<>(stat, entries);
    }
    
    public S getStat() {
        return stat;
    }

    public ImmutableList<Entry<T>> getEntries() {
        return entries;
    }
    
    @Override
    public String toString() {
        return String.format("%s leaders: %d entries", stat.abbrev(), entries.size());
    }


    public static final class Entry<T> {
        private final PlayerId playerId;
        private final T value;
        
        public Entry(PlayerId playerId, T value) {
            this.playerId = requireNonNull(playerId);
            this.value = requireNonNull(value);
        }

        public PlayerId getPlayerId() {
            return playerId;
        }
        
        public T getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return playerId + ": " + value;
        }
    }
}
