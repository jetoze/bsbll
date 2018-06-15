package bsbll.game.params;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;

import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;

public final class OutAdvanceDistribution extends AdvanceDistribution<OutLocation> {

    public OutAdvanceDistribution(
            ImmutableTable<OutLocation, ImmutableSet<Base>, ImmutableMultiset<Advances>> data) {
        super(data);
    }
    
    public static OutAdvanceDistribution defaultAdvances() {
        return new OutAdvanceDistribution(ImmutableTable.of());
    }

    @Override
    protected Advances defaultAdvance(OutLocation key, BaseSituation baseSituation) {
        switch (key) {
        case INFIELD:
            // Move everyone up one base
            return Advances.runnersAdvancesOneBase(baseSituation.getOccupiedBases());
        case OUTFIELD:
            // Let everyone stay put
            return Advances.empty();
        default:
            throw new AssertionError("Unexpected OutLocation: " + key);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder extends BuilderBase<OutLocation, Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        public OutAdvanceDistribution build() {
            return new OutAdvanceDistribution(getData());
        }
    }
}
