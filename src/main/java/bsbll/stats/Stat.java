package bsbll.stats;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

public interface Stat<T> {
    // TODO: Add this
    // String abbrev();
    
    public static interface BasicStat<U extends BasicStat<U>> extends Stat<Integer> {
        default BasicStatValue<U> withValue(int value) {
            return new BasicStatValue<>(self(), value);
        }
        U self();
    }
    
    public static final class BasicStatValue<T extends BasicStat<T>> {
        private final T stat;
        private final int value;
        
        public BasicStatValue(T stat, int value) {
            this.stat = requireNonNull(stat);
            this.value = checkNotNegative(value);
        }

        public T getStat() {
            return stat;
        }

        public int getValue() {
            return value;
        }
    }
}
