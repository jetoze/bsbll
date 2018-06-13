package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.EnumSet;

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
    
    public BaseHitAdvanceDistribution(Table<BaseHit, EnumSet<Base>, Multiset<Advances>> data) {
        this.data = ImmutableTable.copyOf(data);
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
        checkArgument(baseHit != BaseHit.HOMERUN, "Homeruns are not supported");
        Multiset<Advances> possibilities = data.get(baseHit, baseSituation.getOccupiedBases());
        if (possibilities == null || possibilities.isEmpty()) {
            // Highly unlikely, since the data is based on a full season of play.
            // But if it does happen, return a default Advance, by awarding each runner the
            // number of bases given by the base hit.
            // TODO: Implement this.
            throw new UnsupportedOperationException(String.format("Unknown distribution. Hit = %s. Occupied: %s", 
                    baseHit, baseSituation.getOccupiedBases()));
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
