package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;

import bsbll.stats.BattingStat.BasicBattingStat;

@Immutable
public final class BattingStatLine extends StatLine<BasicBattingStat, BattingStatLine> {

    public BattingStatLine() {
        super();
    }

    public BattingStatLine(Map<BasicBattingStat, Integer> values) {
        super(values);
    }
    
    public static BattingStatLine forNewGame() {
        return new BattingStatLine(ImmutableMap.of(BattingStat.GAMES, 1));
    }

    public <T> T get(BattingStat<T> stat) {
        return stat.get(this);
    }
    
    @Override
    protected BattingStatLine newInstance(Map<BasicBattingStat, Integer> values) {
        return new BattingStatLine(values);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {
        private final EnumMap<BasicBattingStat, Integer> values = new EnumMap<>(BasicBattingStat.class);
        
        public Builder set(BasicBattingStat stat, int value) {
            requireNonNull(stat);
            checkNotNegative(value);
            values.put(stat, value);
            return this;
        }
        
        public BattingStatLine build() {
            return new BattingStatLine(values);
        }
    }
}
