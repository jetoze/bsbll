package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.PitchingStat.BasicPitchingStat;

@Immutable
public final class PitchingStatLine extends StatLine<BasicPitchingStat, PitchingStatLine> {
    public PitchingStatLine() {
        super();
    }

    public PitchingStatLine(Map<BasicPitchingStat, Integer> values) {
        super(values);
    }

    public static PitchingStatLine forNewGame() {
        return new PitchingStatLine(ImmutableMap.of(PitchingStat.GAMES, 1));
    }

    public <T> T get(PitchingStat<T> stat) {
        return stat.get(this);
    }
    
    @Override
    protected PitchingStatLine newInstance(Map<BasicPitchingStat, Integer> values) {
        return new PitchingStatLine(values);
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {
        private final EnumMap<BasicPitchingStat, Integer> values = new EnumMap<>(BasicPitchingStat.class);
        
        public Builder set(BasicPitchingStat stat, int value) {
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
