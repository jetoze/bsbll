package bsbll.game.params;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Table;

import bsbll.bases.Advance;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.die.DieFactory;

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
public final class BaseHitAdvanceDistribution {
    // TODO: Should my name be pluralized ("Distributions")?

    // TODO: Use a ImmutableSet<Base> and ImmutableMultiset<Advances>
    // The SortedMultiset is sorted in descending order, so the most likely
    // outcome is first.
    private final ImmutableTable<BaseHit, EnumSet<Base>, Multiset<Advances>> data;
    private final DieFactory dieFactory = DieFactory.random();
    
    /**
     * Creates a {@code BaseHitAdvanceDistribution} based on the distribution
     * data in the given table.
     */
    public BaseHitAdvanceDistribution(Table<BaseHit, EnumSet<Base>, Multiset<Advances>> data) {
        this.data = ImmutableTable.copyOf(data);
    }
    
    /**
     * Creates a {@code BaseHitAdvanceDistribution} that will always advance each runner
     * exactly the number of bases given by the base hit.
     */
    public static BaseHitAdvanceDistribution defaultAdvances() {
        return new BaseHitAdvanceDistribution(ImmutableTable.of());
    }

    /**
     * Rolls the internal (random) die to select one of the possible advances
     * for the given base hit and base situation.
     * 
     * @param baseHit
     *            the base hit (other than {@code BaseHit.HOMERUN}.
     * @param baseSituation
     *            the {@code BaseSituation} at the time of the hit.
     * @param numberOfOuts
     *            the number of outs at the time of the hit.
     * @return an {@code Advances} object that describes the resulting base
     *         advances, including the batter.
     */
    public Advances pickOne(BaseHit baseHit, BaseSituation baseSituation, int numberOfOuts) {
        return pickOne(baseHit, baseSituation, numberOfOuts, this.dieFactory);
    }
    
    /**
     * Rolls a die from the given DieFactory to select one of the possible
     * advances for the given base hit and base situation.
     * 
     * @param baseHit
     *            the base hit (other than {@code BaseHit.HOMERUN}.
     * @param baseSituation
     *            the {@code BaseSituation} at the time of the hit.
     * @param numberOfOuts
     *            the number of outs at the time of the hit.
     * @param dieFactory
     *            the {@code DieFactory} that will be asked to produce the die
     *            to roll
     * @return an {@code Advances} object that describes the resulting base
     *         advances, including the batter.
     */
    public Advances pickOne(BaseHit baseHit, BaseSituation baseSituation, int numberOfOuts, DieFactory dieFactory) {
        requireNonNull(baseHit);
        requireNonNull(baseSituation);
        checkInRange(numberOfOuts, 0, 2);
        requireNonNull(dieFactory);
        if (baseHit == BaseHit.HOMERUN) {
            // Everything is fixed for homeruns
            return defaultAdvance(baseHit, baseSituation);
        }
        Multiset<Advances> possibilities = getPossibilities(baseHit, baseSituation, numberOfOuts);
        if (possibilities == null || possibilities.isEmpty()) {
            return defaultAdvance(baseHit, baseSituation);
        } else {
            return pickOneFromSet(dieFactory, possibilities);
        }
    }
    
    private Advances defaultAdvance(BaseHit baseHit, BaseSituation baseSituation) {
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
    
    private Multiset<Advances> getPossibilities(BaseHit baseHit, BaseSituation baseSituation, int numberOfOuts) {
        // An Advance where two runners are thrown out is not valid if there are already two outs in the inning.
        // TODO: Add number of outs as an additional lookup dimension?
        Multiset<Advances> all = this.data.get(baseHit, baseSituation.getOccupiedBases());
        Multiset<Advances> valid = Multisets.filter(all, a -> (a.getNumberOfOuts() + numberOfOuts) <= 3);
        return valid;
    }

    private Advances pickOneFromSet(DieFactory dieFactory, Multiset<Advances> possibilities) {
        int total = possibilities.size();
        int roll = dieFactory.newDie(total).roll();
        int sum = 0;
        for (Multiset.Entry<Advances> e : possibilities.entrySet()) {
            sum += e.getCount();
            if (roll <= sum) {
                return e.getElement();
            }
        }
        // We should never get here. But in case we do, we pick the most common one.
        return Multisets.copyHighestCountFirst(possibilities).elementSet().iterator().next();
    }
    
    public ImmutableMap<EnumSet<Base>, Multiset<Advances>> forHit(BaseHit hit) {
        requireNonNull(hit);
        ImmutableMap<EnumSet<Base>, Multiset<Advances>> row = this.data.row(hit);
        // TODO: Once we store immutable instances, this copying won't be necessary
        ImmutableMap.Builder<EnumSet<Base>, Multiset<Advances>> copier = ImmutableMap.builder();
        for (Map.Entry<EnumSet<Base>, Multiset<Advances>> e : row.entrySet()) {
            copier.put(e.getKey().clone(), HashMultiset.create(e.getValue()));
        }
        return copier.build();
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private final Table<BaseHit, EnumSet<Base>, Multiset<Advances>> data = HashBasedTable.create();
        
        public Builder add(BaseHit baseHit, BaseSituation situation, Advances advances) {
            requireNonNull(baseHit);
            EnumSet<Base> occupied = situation.getOccupiedBases();
            Multiset<Advances> d = data.get(baseHit, occupied);
            if (d == null) {
                d = HashMultiset.create();
                data.put(baseHit, occupied, d);
            }
            d.add(advances);
            return this;
        }
        
        public BaseHitAdvanceDistribution build() {
            return new BaseHitAdvanceDistribution(data);
        }
    }
}
