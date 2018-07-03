package bsbll.game.params;

import javax.annotation.concurrent.Immutable;

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
 * <p>
 * The current implementation is static in the sense that it doesn't take into
 * account the game context, or the individual speed of the base runners. In
 * reality, for example, the fielding team would be more interested in throwing
 * home, trying to get the lead runner, in the bottom of the ninth inning if the
 * game is tied, compared to if they are up by 10 runs.
 */
@Immutable
public final class OutAdvanceDistribution extends AdvanceDistribution<OutAdvanceKey> {
    private static final OutAdvanceDistribution DEFAULT = new OutAdvanceDistribution(ImmutableTable.of());
    
    public OutAdvanceDistribution(ImmutableTable<OutAdvanceKey, OccupiedBases, ImmutableMultiset<Advances>> data) {
        super(data);
    }

    public static OutAdvanceDistribution defaultAdvances() {
        return DEFAULT;
    }

    @Override
    protected Advances defaultAdvance(OutAdvanceKey key, BaseSituation baseSituation) {
        switch (key.getLocation()) {
        case INFIELD:
            // TODO: This should be an option
            // Move everyone up one base
            //return Advances.runnersAdvancesOneBase(baseSituation.getOccupiedBases());
            return Advances.empty();
        case OUTFIELD:
            // Let everyone stay put
            return Advances.empty();
        default:
            throw new AssertionError("Unexpected OutLocation: " + key);
        }
    }

    @Override
    protected boolean isNumberOfOutsIncludedInKey() {
        return true;
    }

    public void store(Persister p) {
        store(p, OutAdvanceKey::store);
    }
    
    public static OutAdvanceDistribution restoreFrom(Persister p) {
        Builder builder = builder();
        restore(p, builder, OutAdvanceKey::restore);
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder extends BuilderBase<OutAdvanceKey, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        public OutAdvanceDistribution build() {
            return new OutAdvanceDistribution(getData());
        }
    }
}
