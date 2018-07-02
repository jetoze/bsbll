package bsbll.game.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;

import bsbll.bases.Advance;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import p3.Persister;

/**
 * The distributions of possible base-running advances on a base-hit, given the
 * base situation at the time. Only applicable for error-less plays.
 * <p>
 * The current implementation is static in the sense that it doesn't take into
 * account the individual players. In reality, the 1982 Ricky Henderson was
 * probably more likely to advance from first to third on a single, than, say,
 * the 2017 Bartolo Colon.
 */
@Immutable
public final class BaseHitAdvanceDistribution extends AdvanceDistribution<BaseHit> {
    private static final BaseHitAdvanceDistribution DEFAULT = new BaseHitAdvanceDistribution(ImmutableTable.of());
    
    /**
     * Creates a {@code BaseHitAdvanceDistribution} based on the distribution
     * data in the given table.
     */
    public BaseHitAdvanceDistribution(ImmutableTable<BaseHit, OccupiedBases, ImmutableMultiset<Advances>> data) {
        super(data);
    }

    /**
     * Returns a {@code BaseHitAdvanceDistribution} that will always advance each runner
     * exactly the number of bases given by the base hit.
     */
    public static BaseHitAdvanceDistribution defaultAdvances() {
        return DEFAULT;
    }

    @Override
    protected Advances defaultAdvance(BaseHit baseHit, BaseSituation baseSituation) {
        // Note that the default advance will never have any runners out, so
        // the current number of outs does not matter.
        List<Advance> advances = new ArrayList<>();
        advances.add(Base.HOME.defaultAdvance(baseHit)); // the batter
        Arrays.stream(Base.occupiable())
            .filter(baseSituation::isOccupied)
            .map(b -> b.defaultAdvance(baseHit))
            .forEach(advances::add);
        return new Advances(advances);
    }

    @Override
    protected boolean isNumberOfOutsIncludedInKey() {
        return false;
    }

    public void store(Persister p) {
        for (BaseHit key : keySet()) {
            Persister keyPersister = p.newChild("Key")
                    .putString("BaseHit", key.name());
            ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> row = forKey(key);
            for (Map.Entry<OccupiedBases, ImmutableMultiset<Advances>> e : row.entrySet()) {
                Persister entryPersister = keyPersister.newChild("Entry").putString("Bases", e.getKey().name());
                for (Multiset.Entry<Advances> advances : e.getValue().entrySet()) {
                    Persister advancesPersister = entryPersister.newChild("Advances")
                            .putInt("Count", advances.getCount());
                    advances.getElement().store(advancesPersister);
                }
            }
        }
    }
    
    public static BaseHitAdvanceDistribution restoreFrom(Persister p) {
        Builder builder = builder();
        for (Persister keyPersister : p.getChildren("Key")) {
            BaseHit key = BaseHit.valueOf(keyPersister.getString("BaseHit"));
            for (Persister entryPersister : keyPersister.getChildren("Entry")) {
                OccupiedBases occupiedBases = OccupiedBases.valueOf(entryPersister.getString("Bases"));
                for (Persister advancesPersister : entryPersister.getChildren("Advances")) {
                    int count = advancesPersister.getInt("Count");
                    Advances advances = Advances.restoreFrom(advancesPersister);
                    builder.set(key, occupiedBases, advances, count);
                }
            }
        }
        return builder.build();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder extends BuilderBase<BaseHit, Builder> {
        
        @Override
        protected Builder self() {
            return this;
        }

        public BaseHitAdvanceDistribution build() {
            return new BaseHitAdvanceDistribution(getData());
        }
    }
}
