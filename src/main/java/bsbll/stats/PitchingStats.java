package bsbll.stats;

import static bsbll.stats.Pitching.*;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.Pitching.BasicPitching;
import bsbll.stats.Pitching.BasicPitchingValue;

@Immutable
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
    
    /**
     * Creates a PitchingStats instance initialized for a new game. All values
     * are 0 (zero), except for GAMES which is set to 1 (one).
     */
    public static PitchingStats forNewGame() {
        return new PitchingStats(ImmutableMap.of(GAMES, 1));
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
        return this.values.getOrDefault(stat, 0);
    }
    
    public PitchingStats add(PitchingStats o) {
        Map<BasicPitching, Integer> tmp = new HashMap<>(this.values);
        o.values.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return new PitchingStats(tmp);
    }

    public PitchingStats add(BasicPitching stat, int value) {
        return add(stat.withValue(value));
    }
    
    public PitchingStats add(BasicPitching stat1, int value1, BasicPitching stat2, int value2) {
        return add(stat1.withValue(value1), stat2.withValue(value2));
    }
    
    public PitchingStats add(BasicPitching stat1, int value1, BasicPitching stat2, int value2,
            BasicPitching stat3, int value3) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3));
    }
    
    public PitchingStats add(BasicPitching stat1, int value1, BasicPitching stat2, int value2,
            BasicPitching stat3, int value3, BasicPitching stat4, int value4) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3),
                stat4.withValue(value4));
    }
    
    public PitchingStats add(BasicPitching stat1, int value1, BasicPitching stat2, int value2,
            BasicPitching stat3, int value3, BasicPitching stat4, int value4,
            BasicPitching stat5, int value5) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3),
                stat4.withValue(value4), stat5.withValue(value5));
    }
    
    public PitchingStats add(BasicPitchingValue... values) {
        Map<BasicPitching, Integer> tmp = new HashMap<>(this.values);
        Arrays.stream(values).forEach(v -> tmp.merge(v.getStat(), v.getValue(), (p, q) -> p + q));
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
