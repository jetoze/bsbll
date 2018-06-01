package bsbll.stats;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

abstract class AbstractStat<T> implements Stat<T> {
    private final String abbrev;
    private final Comparator<T> leaderOrder;
    
    protected AbstractStat(String abbrev, Comparator<T> leaderOrder) {
        this.abbrev = requireNonNull(abbrev);
        this.leaderOrder = requireNonNull(leaderOrder);
    }

    @Override
    public Comparator<T> leaderOrder() {
        return leaderOrder;
    }

    @Override
    public final String abbrev() {
        return abbrev;
    }

    @Override
    public String toString() {
        return this.abbrev;
    }
    
    abstract static class AbstractBattingStat<T> extends AbstractStat<T> implements BattingStat<T> {
        protected AbstractBattingStat(String abbrev, Comparator<T> leaderOrder) {
            super(abbrev, leaderOrder);
        }
    }
    
    abstract static class AbstractPitchingStat<T> extends AbstractStat<T> implements PitchingStat<T> {
        protected AbstractPitchingStat(String abbrev, Comparator<T> leaderOrder) {
            super(abbrev, leaderOrder);
        }
    }
}
