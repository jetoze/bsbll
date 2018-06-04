package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import bsbll.player.PlayerId;

public final class BattingLeaders<T> { // TODO: Replace with a generic StatLeaders
    private final BattingStat<T> stat;
    private final ImmutableList<Entry<T>> entries;
    
    public BattingLeaders(BattingStat<T> stat, List<Entry<T>> entries) {
        this.stat = requireNonNull(stat);
        this.entries = ImmutableList.copyOf(entries);
    }
    
    public static <T> BattingLeaders<T> forStat(Map<PlayerId, BattingStatLine> statLines,
            BattingStat<T> stat, int top) {
        return forStat(statLines, stat, top, i -> true);
    }
    
    public static <T> BattingLeaders<T> forStat(Map<PlayerId, BattingStatLine> statLines,
            BattingStat<T> stat, int top, int minAtBats) {
        checkPositive(minAtBats);
        return forStat(statLines, stat, top, e -> e.getValue().get(BattingStat.AT_BATS) >= minAtBats);
    }
    
    public static <T> BattingLeaders<T> forStat(Map<PlayerId, BattingStatLine> statLines,
            BattingStat<T> stat, int top, Predicate<Map.Entry<PlayerId, BattingStatLine>> filter) {
        requireNonNull(stat);
        checkPositive(top);
        requireNonNull(filter);
        List<Entry<T>> entries = statLines.entrySet().stream()
                .filter(filter)
                .map(e -> new Entry<>(e.getKey(), e.getValue().get(stat)))
                .sorted(Comparator.comparing(Entry::getValue, stat.leaderOrder()))
                .limit(top)
                .collect(toList());
        return new BattingLeaders<>(stat, entries);
    }
    
    
    
    public Stat<T> getStat() {
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
