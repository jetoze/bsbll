package bsbll.stats;

import static bsbll.stats.Batting.*;
import static com.google.common.base.Preconditions.checkArgument;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import bsbll.matchup.MatchupRunner.Outcome;

public final class BattingStats {
    private final ImmutableMap<Batting, Integer> values;
    
    // TODO: Come up with a better data representation of this? Something like
    // Map<StatEnum, Integer>
    //       ^
    //       |-- Or CountingStatEnum
    
    public BattingStats() {
        this.values = ImmutableMap.of();
    }
    
    public BattingStats(Map<Batting, Integer> values) {
        this.values = ImmutableMap.copyOf(values);
        checkArgument(this.values.keySet().stream().noneMatch(Batting::isDerived), "Only non-derived stats may be added");
        checkArgument(this.values.values().stream().allMatch(i -> i >= 0), "Negative values are not allowed");
        // TODO: Additional preconditions here.
    }
    
    public BattingStats(int plateAppearances, int hits, int doubles, int triples, int homeruns,
            int walks, int strikeouts, int hitByPitches) {
        // TODO: Preconditions. No negatives. The sum of (hits + walks + strikeouts + hitByPitches)
        // must be <= plateAppearances.
        this.values = ImmutableMap.<Batting, Integer>builder()
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

    public int getPlateAppearances() {
        return get(PLATE_APPEARANCES);
    }

    public int getAtBats() {
        return get(Batting.PLATE_APPEARANCES) - get(WALKS) - get(Batting.HIT_BY_PITCHES) - 
                get(Batting.SACRIFICE_HITS) - get(SACRIFICE_FLIES);
    }
    
    public Average getBattingAverage() {
        return new Average(get(HITS), getAtBats());
    }
    
    public Average getSluggingPercentage() {
        return new Average(getTotalBases(), getAtBats());
    }
    
    public Average getOnBasePercentage() {
        return new Average(get(HITS) + get(WALKS) + get(Batting.HIT_BY_PITCHES),
                get(PLATE_APPEARANCES) - get(Batting.SACRIFICE_HITS));
    }
    
    public Average getOps() {
        return Average.sumOf(getOnBasePercentage(), getSluggingPercentage());
    }
    
    public int getExtraBaseHits() {
        return get(DOUBLES) + get(TRIPLES) + get(HOMERUNS);
    }
    
    public int getSingles() {
        return get(HITS) - getExtraBaseHits();
    }
    
    public int getTotalBases() {
        return get(HITS) + get(DOUBLES) + 2 * get(TRIPLES) + 3 * get(HOMERUNS);
    }

    public BattingStats add(Outcome outcome) {
        int pa = get(PLATE_APPEARANCES) + 1;
        int h = get(HITS) + (outcome.isHit() ? 1 : 0);
        int d = get(DOUBLES) + (outcome == Outcome.DOUBLE ? 1 : 0);
        int t = get(TRIPLES) + (outcome == Outcome.TRIPLE ? 1 : 0);
        int hr = get(HOMERUNS) + (outcome == Outcome.HOMERUN ? 1 : 0);
        int w = get(WALKS) + (outcome == Outcome.WALK ? 1 : 0);
        int so = get(STRIKEOUTS) + (outcome == Outcome.STRIKEOUT ? 1 : 0);
        int hbp = get(HIT_BY_PITCHES) + (outcome == Outcome.HIT_BY_PITCH ? 1 : 0);
        return new BattingStats(pa, h, d, t, hr, w, so, hbp);
    }
    
    public BattingStats add(BattingStats o) {
        Map<Batting, Integer> tmp = new HashMap<>(this.values);
        o.values.forEach((s, v) -> tmp.merge(s, v, (p, q) -> p + q));
        return new BattingStats(tmp);
    }
    
    public int get(Batting stat) {
        checkArgument(!stat.isDerived());
        return this.values.getOrDefault(stat, 0);
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
        TreeMap<Batting, Integer> sorted = new TreeMap<>(this.values);
        return sorted.toString();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final EnumMap<Batting, Integer> values = new EnumMap<>(Batting.class);
        
        public Builder set(Batting stat, int value) {
            checkArgument(!stat.isDerived(), "Derived stats cannot be set directly");
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public BattingStats build() {
            return new BattingStats(values);
        }
    }
    
}
