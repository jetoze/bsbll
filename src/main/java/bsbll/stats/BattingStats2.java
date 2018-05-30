package bsbll.stats;

import static bsbll.stats.BattingStat.*;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.BattingStat.CountedBattingStat;

public final class BattingStats2 {
    private final ImmutableMap<CountedBattingStat, Integer> values;
    
    public BattingStats2() {
        this.values = ImmutableMap.of();
    }
   
    public BattingStats2(Map<CountedBattingStat, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
    }
    
    public BattingStats2(int plateAppearances, int hits, int doubles, int triples, int homeruns,
            int walks, int strikeouts, int hitByPitches) {
        // TODO: Preconditions. No negatives. The sum of (hits + walks + strikeouts + hitByPitches)
        // must be <= plateAppearances.
        this.values = ImmutableMap.<CountedBattingStat, Integer>builder()
                .put(PLATE_APPEARANCES, plateAppearances)
                .put(HITS, hits)
                .put(DOUBLES, doubles)
                .put(TRIPLES, triples)
                .put(HOMERUNS, homeruns)
                .put(WALKS, walks)
                .put(STRIKEOUTS, strikeouts)
                .put(HIT_BY_PITCHES, hitByPitches)
                .build();
    }

    public <T> T get(BattingStat<T> stat) {
        return stat.get(this);
    }

    /**
     * Package-private method used by the CountedBattingStat enum to lookup the
     * corresponding value.
     */
    int getCountedStat(CountedBattingStat stat) {
        requireNonNull(stat);
        return this.values.getOrDefault(stat, 0);
    }
    
    public BattingStats2 add(BattingStats2 o) {
        Map<CountedBattingStat, Integer> tmp = new HashMap<>(this.values);
        o.values.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return new BattingStats2(tmp);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
                ((obj instanceof BattingStats2) && this.values.equals(((BattingStats2) obj).values));
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        TreeMap<CountedBattingStat, Integer> sorted = new TreeMap<>(this.values);
        return sorted.toString();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final EnumMap<CountedBattingStat, Integer> values = new EnumMap<>(CountedBattingStat.class);
        
        public Builder set(CountedBattingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public BattingStats2 build() {
            return new BattingStats2(values);
        }
    }
}
