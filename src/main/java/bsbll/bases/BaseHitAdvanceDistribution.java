package bsbll.bases;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Table;

import bsbll.die.DieFactory;

/**
 * The distributions of possible base-running advances on a base-hit, given the
 * base situation at the time. Only applicable for error-less plays.
 * <p>
 * Note that the outcome is always fixed for a homerun (all runners score), so this
 * class is applicable only to singles, doubles, and triples.
 */
@Immutable
public final class BaseHitAdvanceDistribution {
    // TODO: Should I be in this package?
    // TODO: Should my name be pluralized ("Distributions")?

    // TODO: Use a ImmutableSet<Base> and ImmutableMultiset<Advances>
    // The SortedMultiset is sorted in descending order, so the most likely
    // outcome is first.
    private final ImmutableTable<BaseHit, EnumSet<Base>, Multiset<Advances>> data;
    
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
     * Rolls a die to select one of the possible advances for the given base hit
     * and base situation.
     * 
     * @param baseHit
     *            the base hit (other than {@code BaseHit.HOMERUN}.
     * @param baseSituation
     *            the {@code BaseSituation} at the time of the hit.
     * @param dieFactory
     *            the {@code DieFactory} that will be asked to produce the die
     *            to roll
     * @return an {@code Advances} object that describes the resulting base
     *         advances, including the batter.
     */
    public Advances pickOne(BaseHit baseHit, BaseSituation baseSituation, DieFactory dieFactory) {
        requireNonNull(baseHit);
        requireNonNull(baseSituation);
        requireNonNull(dieFactory);
        if (baseHit == BaseHit.HOMERUN) {
            // Everything is fixed for homeruns
            return defaultAdvance(baseHit, baseSituation);
        }
        Multiset<Advances> possibilities = data.get(baseHit, baseSituation.getOccupiedBases());
        if (possibilities == null || possibilities.isEmpty()) {
            return defaultAdvance(baseHit, baseSituation);
        }
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
    
    private Advances defaultAdvance(BaseHit baseHit, BaseSituation baseSituation) {
        List<Advance> advances = new ArrayList<>();
        advances.add(Base.HOME.defaultAdvance(baseHit)); // the batter
        Arrays.stream(Base.occupiable())
            .filter(baseSituation::isOccupied)
            .map(b -> b.defaultAdvance(baseHit))
            .forEach(advances::add);
        return new Advances(advances);
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
