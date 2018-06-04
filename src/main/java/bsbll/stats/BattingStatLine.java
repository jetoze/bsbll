package bsbll.stats;

import static bsbll.stats.BattingStat.*;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.stats.BattingStat.PrimitiveBattingStat;

@Immutable
public final class BattingStatLine extends StatLine<PrimitiveBattingStat, BattingStatLine> {

    public BattingStatLine() {
        super();
    }

    public BattingStatLine(Map<PrimitiveBattingStat, Integer> values) {
        super(values);
    }

    public <T> T get(BattingStat<T> stat) {
        return stat.get(this);
    }
    
    @Override
    protected BattingStatLine newInstance(Map<PrimitiveBattingStat, Integer> values) {
        return new BattingStatLine(values);
    }
    
    public BattingStatLine plus(Outcome o) {
        return plus(o, 0);
    }
    
    public BattingStatLine plus(Outcome o, int rbis) {
        requireNonNull(o);
        checkNotNegative(rbis);
        return builder()
                .set(PLATE_APPEARANCES, get(PLATE_APPEARANCES) + 1)
                .set(HITS, get(HITS) + (o.isHit() ? 1 : 0))
                .set(DOUBLES, get(DOUBLES) + (o == Outcome.DOUBLE ? 1 : 0))
                .set(TRIPLES, get(TRIPLES) + (o == Outcome.TRIPLE ? 1 : 0))
                .set(HOMERUNS, get(HOMERUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                .set(RUNS, get(RUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                .set(RUNS_BATTED_IN, get(RUNS_BATTED_IN) + rbis)
                .set(WALKS, get(WALKS) + (o == Outcome.WALK ? 1 : 0))
                .set(STRIKEOUTS, get(STRIKEOUTS) + (o == Outcome.STRIKEOUT ? 1 : 0))
                .set(HIT_BY_PITCHES, get(HIT_BY_PITCHES) + (o == Outcome.HIT_BY_PITCH ? 1 : 0))
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder forNewGame() {
        return builder().set(GAMES, 1);
    }

    public static final class Builder {
        private final EnumMap<PrimitiveBattingStat, Integer> values = new EnumMap<>(PrimitiveBattingStat.class);
        
        public Builder set(PrimitiveBattingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public Builder add(PrimitiveBattingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            if (value > 0) {
                values.put(stat, get(stat) + value);
            }
            return this;
        }
        
        public Builder add(Outcome o, int rbis) {
            requireNonNull(o);
            checkNotNegative(rbis);
            return set(PLATE_APPEARANCES, get(PLATE_APPEARANCES) + 1)
                    .set(HITS, get(HITS) + (o.isHit() ? 1 : 0))
                    .set(DOUBLES, get(DOUBLES) + (o == Outcome.DOUBLE ? 1 : 0))
                    .set(TRIPLES, get(TRIPLES) + (o == Outcome.TRIPLE ? 1 : 0))
                    .set(HOMERUNS, get(HOMERUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                    .set(RUNS, get(RUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                    .set(RUNS_BATTED_IN, get(RUNS_BATTED_IN) + rbis)
                    .set(WALKS, get(WALKS) + (o == Outcome.WALK ? 1 : 0))
                    .set(STRIKEOUTS, get(STRIKEOUTS) + (o == Outcome.STRIKEOUT ? 1 : 0))
                    .set(HIT_BY_PITCHES, get(HIT_BY_PITCHES) + (o == Outcome.HIT_BY_PITCH ? 1 : 0));
        }
        
        private int get(PrimitiveBattingStat stat) {
            return values.getOrDefault(stat, 0);
        }

        public BattingStatLine build() {
            return new BattingStatLine(values);
        }
    }
}
