package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.PitchingStat.PrimitivePitchingStat;

@Immutable
public final class PitchingStatLine extends StatLine<PrimitivePitchingStat, PitchingStatLine> {
    public PitchingStatLine() {
        super();
    }

    public PitchingStatLine(Map<PrimitivePitchingStat, Integer> values) {
        super(values);
    }

    public static PitchingStatLine forNewGame() {
        return new PitchingStatLine(ImmutableMap.of(PitchingStat.GAMES, 1));
    }

    public <T> T get(PitchingStat<T> stat) {
        return stat.get(this);
    }
    
    @Override
    protected PitchingStatLine newInstance(Map<PrimitivePitchingStat, Integer> values) {
        return new PitchingStatLine(values);
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {
        private final EnumMap<PrimitivePitchingStat, Integer> values = new EnumMap<>(PrimitivePitchingStat.class);
        
        public Builder set(PrimitivePitchingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public PitchingStatLine build() {
            return new PitchingStatLine(values);
        }
    }
}
