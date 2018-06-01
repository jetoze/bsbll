package bsbll.stats;

import static java.util.Objects.requireNonNull;

abstract class AbstractStat<T> implements Stat<T> {
    private final String abbrev;
    
    protected AbstractStat(String abbrev) {
        this.abbrev = requireNonNull(abbrev);
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
        protected AbstractBattingStat(String abbrev) {
            super(abbrev);
        }
    }
    
    abstract static class AbstractPitchingStat<T> extends AbstractStat<T> implements PitchingStat<T> {
        protected AbstractPitchingStat(String abbrev) {
            super(abbrev);
        }
    }
}
