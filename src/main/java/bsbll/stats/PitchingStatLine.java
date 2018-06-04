package bsbll.stats;

import static bsbll.stats.PitchingStat.*;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

@Immutable
public final class PitchingStatLine extends StatLine<PrimitivePitchingStat, PitchingStatLine> {
    public PitchingStatLine() {
        super();
    }

    public PitchingStatLine(Map<PrimitivePitchingStat, Integer> values) {
        super(values);
    }

    public <T> T get(PitchingStat<T> stat) {
        return stat.get(this);
    }
    
    public PitchingStatLine plus(Outcome o, int runs) {
        requireNonNull(o);
        checkNotNegative(runs);
        return builder()
                .set(BATTERS_FACED, get(BATTERS_FACED) + 1)
                .set(OUTS, get(OUTS) + (o.isOut() ? 1 : 0))
                .set(HITS, get(HITS) + (o.isHit() ? 1 : 0))
                .set(HOMERUNS, get(HOMERUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                .set(EARNED_RUNS, get(EARNED_RUNS) + runs)
                .set(WALKS, get(WALKS) + (o == Outcome.WALK ? 1 : 0))
                .set(STRIKEOUTS, get(STRIKEOUTS) + (o == Outcome.STRIKEOUT ? 1 : 0))
                .set(HIT_BY_PITCHES, get(HIT_BY_PITCHES) + (o == Outcome.HIT_BY_PITCH ? 1 : 0))
                .build();
    }

    @Override
    protected PitchingStatLine newInstance(Map<PrimitivePitchingStat, Integer> values) {
        return new PitchingStatLine(values);
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder forNewGame() {
        return builder().set(GAMES, 1);
    }

    public static final class Builder {
        private final EnumMap<PrimitivePitchingStat, Integer> values = new EnumMap<>(PrimitivePitchingStat.class);
        
        public Builder set(PrimitivePitchingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public Builder add(Outcome o, int runs) {
            requireNonNull(o);
            checkNotNegative(runs);
            return set(BATTERS_FACED, get(BATTERS_FACED) + 1)
                    .set(OUTS, get(OUTS) + (o.isOut() ? 1 : 0))
                    .set(HITS, get(HITS) + (o.isHit() ? 1 : 0))
                    .set(HOMERUNS, get(HOMERUNS) + (o == Outcome.HOMERUN ? 1 : 0))
                    .set(EARNED_RUNS, get(EARNED_RUNS) + runs)
                    .set(WALKS, get(WALKS) + (o == Outcome.WALK ? 1 : 0))
                    .set(STRIKEOUTS, get(STRIKEOUTS) + (o == Outcome.STRIKEOUT ? 1 : 0))
                    .set(HIT_BY_PITCHES, get(HIT_BY_PITCHES) + (o == Outcome.HIT_BY_PITCH ? 1 : 0));
        }

        private int get(PrimitivePitchingStat stat) {
            return values.getOrDefault(stat, 0);
        }

        public PitchingStatLine build() {
            return new PitchingStatLine(values);
        }
    }
}
