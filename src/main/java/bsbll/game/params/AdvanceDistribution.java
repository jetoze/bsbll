package bsbll.game.params;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Table;

import bsbll.bases.Advances;
import bsbll.bases.BaseSituation;
import bsbll.bases.OccupiedBases;
import bsbll.die.DieFactory;

/**
 * The distributions of possible base-running advances for a set of event types,
 * given the base situation at the time.
 * 
 * @param <E> the type that defines the key-space (event types).
 */
public abstract class AdvanceDistribution<E> {
    private final ImmutableTable<E, OccupiedBases, ImmutableMultiset<Advances>> data;
    
    protected AdvanceDistribution(ImmutableTable<E, OccupiedBases, ImmutableMultiset<Advances>> data) {
        this.data = requireNonNull(data);
    }
    
    /**
     * Rolls a die from the given DieFactory to select one of the possible
     * advances for the given key and base situation.
     * 
     * @param key
     *            the key
     * @param baseSituation
     *            the {@code BaseSituation} at the time of the event.
     * @param numberOfOuts
     *            the number of outs at the time of the event.
     * @param dieFactory
     *            the {@code DieFactory} that will be asked to produce the die
     *            to roll
     * @return an {@code Advances} object that describes the resulting base
     *         advances, including the batter.
     */
    public final Advances pickOne(E key, BaseSituation baseSituation, int numberOfOuts, DieFactory dieFactory) {
        requireNonNull(key);
        requireNonNull(baseSituation);
        checkInRange(numberOfOuts, 0, 2);
        requireNonNull(dieFactory);
        Multiset<Advances> possibilities = getPossibilities(key, baseSituation, numberOfOuts);
        return possibilities.isEmpty()
                ? defaultAdvance(key, baseSituation)
                : pickOneFromSet(dieFactory, possibilities);
    }
    
    /**
     * Called for a combination of key and base situation that is not known by
     * this distribution. This can be used in testing purposes, when spinning up
     * simple environments that are not based on real data, or to handle rare
     * corner cases that were not covered by the data (e.g. play-by-play files)
     * from which a distribution is built.
     */
    protected abstract Advances defaultAdvance(E key, BaseSituation baseSituation);
    
    private Multiset<Advances> getPossibilities(E key, BaseSituation baseSituation, int numberOfOuts) {
        // An Advance where two runners are thrown out is not valid if there are already two outs in the inning.
        // TODO: Add number of outs as an additional lookup dimension?
        ImmutableMultiset<Advances> all = this.data.get(key, baseSituation.getOccupiedBases());
        if (all == null) {
            return ImmutableMultiset.of();
        } else if(isNumberOfOutsIncludedInKey()) {
            return all;
        } else {
            Multiset<Advances> valid = Multisets.filter(all, a -> (a.getNumberOfOuts() + numberOfOuts) <= 3);
            return valid;
        }
    }
    
    // TODO: Should we always require that the key is included?
    protected abstract boolean isNumberOfOutsIncludedInKey();

    private Advances pickOneFromSet(DieFactory dieFactory, Multiset<Advances> possibilities) {
        int total = possibilities.size();
        int roll = dieFactory.getDie(total).roll();
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
    
    public final ImmutableSet<E> keys() {
        return this.data.rowKeySet();
    }
    
    public final ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> forKey(E key) {
        requireNonNull(key);
        checkArgument(this.data.containsRow(key), "Unknown key: %s", key);
        return this.data.row(key);
    }
    
    public static abstract class BuilderBase<E, B extends BuilderBase<E, B>> {
        private final Table<E, OccupiedBases, Multiset<Advances>> data = HashBasedTable.create();
        
        public B add(E key, BaseSituation situation, Advances advances) {
            requireNonNull(key);
            OccupiedBases occupied = situation.getOccupiedBases();
            Multiset<Advances> d = data.get(key, occupied);
            if (d == null) {
                d = HashMultiset.create();
                data.put(key, occupied, d);
            }
            d.add(advances);
            return self();
        }
        
        protected abstract B self();
        
        protected final ImmutableTable<E, OccupiedBases, ImmutableMultiset<Advances>> getData() {
            ImmutableTable.Builder<E, OccupiedBases, ImmutableMultiset<Advances>> tableBuilder = ImmutableTable.builder();
            for (Entry<E, Map<OccupiedBases, Multiset<Advances>>> rme : data.rowMap().entrySet()) {
                E key = rme.getKey();
                for (Entry<OccupiedBases, Multiset<Advances>> re : rme.getValue().entrySet()) {
                    Multiset<Advances> advances = re.getValue();
                    tableBuilder.put(
                            key,
                            re.getKey(),
                            ImmutableMultiset.copyOf(advances));
                }
            }
            return tableBuilder.build();
        }
    }
}
