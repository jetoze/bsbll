package bsbll.stats;

import static bsbll.stats.Batting.*;
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

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.stats.Batting.BasicBatting;
import bsbll.stats.Batting.BasicBattingValue;

/**
 * A collection of batting stats, and their values.
 */
@Immutable
public final class BattingStats {
    private final ImmutableMap<BasicBatting, Integer> values;
    
    public BattingStats() {
        this.values = ImmutableMap.of();
    }
   
    public BattingStats(Map<BasicBatting, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
    }
    
    public BattingStats(int plateAppearances, int hits, int doubles, int triples, int homeruns,
            int walks, int strikeouts, int hitByPitches) {
        this(ImmutableMap.<BasicBatting, Integer>builder()
                .put(PLATE_APPEARANCES, plateAppearances)
                .put(HITS, hits)
                .put(DOUBLES, doubles)
                .put(TRIPLES, triples)
                .put(HOMERUNS, homeruns)
                .put(WALKS, walks)
                .put(STRIKEOUTS, strikeouts)
                .put(HIT_BY_PITCHES, hitByPitches)
                .build());
    }

    /**
     * Creates a BattingStats instance initialized for a new game. All values
     * are 0 (zero), except for GAMES which is set to 1 (one).
     */
    public static BattingStats forNewGame() {
        return new BattingStats(ImmutableMap.of(GAMES, 1));
    }

    public <T> T get(Batting<T> stat) {
        return stat.get(this);
    }

    /**
     * Package-private method used by the BasicBatting enum to lookup the
     * corresponding value.
     */
    int getBasicStat(BasicBatting stat) {
        requireNonNull(stat);
        return this.values.getOrDefault(stat, 0);
    }
    
    public BattingStats add(BattingStats o) {
        Map<BasicBatting, Integer> tmp = new HashMap<>(this.values);
        o.values.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return new BattingStats(tmp);
    }

    public BattingStats add(BasicBatting stat, int value) {
        return add(stat.withValue(value));
    }
    
    public BattingStats add(BasicBatting stat1, int value1, BasicBatting stat2, int value2) {
        return add(stat1.withValue(value1), stat2.withValue(value2));
    }
    
    public BattingStats add(BasicBatting stat1, int value1, BasicBatting stat2, int value2,
            BasicBatting stat3, int value3) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3));
    }
    
    public BattingStats add(BasicBatting stat1, int value1, BasicBatting stat2, int value2,
            BasicBatting stat3, int value3, BasicBatting stat4, int value4) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3),
                stat4.withValue(value4));
    }
    
    public BattingStats add(BasicBatting stat1, int value1, BasicBatting stat2, int value2,
            BasicBatting stat3, int value3, BasicBatting stat4, int value4,
            BasicBatting stat5, int value5) {
        return add(stat1.withValue(value1), stat2.withValue(value2), stat3.withValue(value3),
                stat4.withValue(value4), stat5.withValue(value5));
    }
    
    public BattingStats add(BasicBattingValue... values) {
        Map<BasicBatting, Integer> tmp = new HashMap<>(this.values);
        Arrays.stream(values).forEach(v -> tmp.merge(v.getStat(), v.getValue(), (p, q) -> p + q));
        return new BattingStats(tmp);
    }
    
    public BattingStats add(Outcome o) {
        int pa = get(PLATE_APPEARANCES) + 1;
        int h = get(HITS) + (o.isHit() ? 1 : 0);
        int db = get(DOUBLES) + (o == Outcome.DOUBLE ? 1 : 0);
        int tp = get(TRIPLES) + (o == Outcome.TRIPLE ? 1 : 0);
        int hr = get(HOMERUNS) + (o == Outcome.HOMERUN ? 1 : 0);
        int bb = get(WALKS) + (o == Outcome.WALK ? 1 : 0);
        int so = get(STRIKEOUTS) + (o == Outcome.STRIKEOUT ? 1 : 0);
        int hbp = get(HIT_BY_PITCHES) + (o == Outcome.HIT_BY_PITCH ? 1 : 0);
        return new BattingStats(pa, h, db, tp, hr, bb, so, hbp);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
                ((obj instanceof BattingStats) && this.values.equals(((BattingStats) obj).values));
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        TreeMap<BasicBatting, Integer> sorted = new TreeMap<>(this.values);
        return sorted.toString();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final EnumMap<BasicBatting, Integer> values = new EnumMap<>(BasicBatting.class);
        
        public Builder set(BasicBatting stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public BattingStats build() {
            return new BattingStats(values);
        }
    }
}
