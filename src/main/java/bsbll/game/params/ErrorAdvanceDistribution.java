package bsbll.game.params;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableTable;

import bsbll.bases.Advances;
import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import p3.Persister;

/**
 * The distributions of possible base-running advances on an out or fielder's
 * choice, given the base situation at the time. Only applicable for error-less
 * plays.
 */
public final class ErrorAdvanceDistribution extends AdvanceDistribution<ErrorAdvanceKey> {
    private static ErrorAdvanceDistribution DEFAULT = new ErrorAdvanceDistribution(ImmutableTable.of());
    
    public ErrorAdvanceDistribution(ImmutableTable<ErrorAdvanceKey, OccupiedBases, ImmutableMultiset<Advances>> data) {
        super(data);
    }
    
    public static ErrorAdvanceDistribution defaultAdvances() {
        return DEFAULT;
    }

    @Override
    protected Advances defaultAdvance(ErrorAdvanceKey key, BaseSituation baseSituation) {
        // Advance each runner one base per error.
        return Advances.advanceAllRunners(baseSituation.getOccupiedBases(), key.getNumberOfErrors());
    }

    @Override
    protected boolean isNumberOfOutsIncludedInKey() {
        return true;
    }
    
    public static ErrorAdvanceDistribution restoreFrom(Persister p) {
        Builder builder = builder();
        restore(p, builder, ErrorAdvanceKey::restore);
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder extends BuilderBase<ErrorAdvanceKey, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        public ErrorAdvanceDistribution build() {
            return new ErrorAdvanceDistribution(getData());
        }
    }
}
