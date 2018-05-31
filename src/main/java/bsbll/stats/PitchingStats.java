package bsbll.stats;

import static bsbll.stats.Pitching.*;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.Pitching.BasicPitching;

public final class PitchingStats {
    private final ImmutableMap<BasicPitching, Integer> values;
    
    public PitchingStats() {
        this.values = ImmutableMap.of();
    }
    
    public PitchingStats(Map<BasicPitching, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
    }
    
    public PitchingStats(int battersFaced,
                         int outs,
                         int hits,
                         int homeruns,
                         int walks,
                         int strikeouts,
                         int hitByPitches) {
        this(ImmutableMap.<BasicPitching, Integer>builder()
                .put(BATTERS_FACED, battersFaced)
                .put(OUTS, outs)
                .put(HITS, hits)
                .put(HOMERUNS, homeruns)
                .put(WALKS, walks)
                .put(STRIKEOUTS, strikeouts)
                .put(HIT_BY_PITCHES, hitByPitches)
                .build());
    }
    
    public <T> T get(Pitching<T> stat) {
        return stat.get(this);
    }
    
    /**
     * Package-private method used by the BasicPitching enum to lookup the
     * corresponding value.
     */
    int getBasicStat(BasicPitching stat) {
        requireNonNull(stat);
        throw new RuntimeException("TODO: Implement me");
    }
    
    public PitchingStats add(PitchingStats o) {
        Map<BasicPitching, Integer> tmp = new HashMap<>(this.values);
        o.values.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return new PitchingStats(tmp);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
                ((obj instanceof PitchingStats) && this.values.equals(((PitchingStats) obj).values));
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        TreeMap<BasicPitching, Integer> sorted = new TreeMap<>(this.values);
        return sorted.toString();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final EnumMap<BasicPitching, Integer> values = new EnumMap<>(BasicPitching.class);
        
        public Builder set(BasicPitching stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public PitchingStats build() {
            return new PitchingStats(values);
        }
    }
}
