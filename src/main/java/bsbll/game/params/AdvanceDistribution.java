package bsbll.game.params;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

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
import p3.Persister;

/**
 * The distributions of possible base-running advances for a set of event types,
 * given the base situation at the time.
 * 
 * @param <E> the type that defines the key-space (event types).
 */
public abstract class AdvanceDistribution<E extends AdvanceDistributionKey> {
    private static final Predicate<? super Advances> ALL = a -> true;
    
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
     * @param dieFactory
     *            the {@code DieFactory} that will be asked to produce the die
     *            to roll
     * @return an {@code Advances} object that describes the resulting base
     *         advances, including the batter.
     */
    public final Advances pickOne(E key, BaseSituation baseSituation, DieFactory dieFactory) {
        requireNonNull(key);
        requireNonNull(baseSituation);
        requireNonNull(dieFactory);
        Multiset<Advances> possibilities = getPossibilities(key, baseSituation, ALL);
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
    
    /**
     * Returns the candidates for the given key and base situtation. Candidates that would result in 
     * more than three outs in the inning are excluded.
     */
    private Multiset<Advances> getPossibilities(E key, 
                                                BaseSituation baseSituation, 
                                                Predicate<? super Advances> predicate) {
        // An Advance where two runners are thrown out is not valid if there are already two outs in the inning.
        // TODO: Add number of outs as an additional lookup dimension?
        ImmutableMultiset<Advances> all = this.data.get(key, baseSituation.getOccupiedBases());
        if (all == null) {
            return ImmutableMultiset.of();
        } else {
            return (predicate == ALL)
                    ? all
                    : Multisets.filter(all, a -> predicate.test(a));
        }
    }

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
        return mostCommon(possibilities);
    }

    private Advances mostCommon(Multiset<Advances> possibilities) {
        assert !possibilities.isEmpty();
        return Multisets.copyHighestCountFirst(possibilities).elementSet().iterator().next();
    }
    
    public final Advances pickMostCommon(E key, BaseSituation baseSituation) {
        return pickMostCommon(key, baseSituation, ALL);
    }
    
    public final Advances pickMostCommon(E key, 
                                         BaseSituation baseSituation, 
                                         Predicate<? super Advances> predicate) {
        requireNonNull(predicate);
        Multiset<Advances> possibilities = getPossibilities(key, baseSituation, predicate);
        return possibilities.isEmpty()
                ? defaultAdvance(key, baseSituation)
                : mostCommon(possibilities);
    }
    
    public final ImmutableSet<E> keySet() {
        return this.data.rowKeySet();
    }
    
    public final ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> forKey(E key) {
        requireNonNull(key);
        checkArgument(this.data.containsRow(key), "Unknown key: %s", key);
        return this.data.row(key);
    }
    
    @Override
    public boolean equals(Object obj) {
        // XXX: This feels a bit icky, but should be safe. It would break once we have a subclass that
        // adds additional properties, but I don't see why we would ever have such a subclass.
        return (obj == this) ||
                ((obj instanceof AdvanceDistribution) && this.data.equals(((AdvanceDistribution<?>) obj).data));
    }
    
    @Override
    public int hashCode() {
        // XXX: See equals
        return data.hashCode();
    }

    public final void store(Persister p) {
        Storage.store(this, p);
    }

    protected static final <E, B extends BuilderBase<E, B>> void restore(Persister p, B builder, Function<Persister, E> keyReader) {
        Storage.restore(p, builder, keyReader);
    }
    
    public static abstract class BuilderBase<E, B extends BuilderBase<E, B>> {
        private final Table<E, OccupiedBases, Multiset<Advances>> data = HashBasedTable.create();
        
        public B add(E key, BaseSituation situation, Advances advances) {
            return add(key, situation.getOccupiedBases(), advances);
        }
        
        public B add(E key, OccupiedBases occupiedBases, Advances advances) {
            getMultiset(key, occupiedBases).add(advances);
            return self();
        }

        private Multiset<Advances> getMultiset(E key, OccupiedBases occupied) {
            requireNonNull(key);
            Multiset<Advances> d = data.get(key, occupied);
            if (d == null) {
                d = HashMultiset.create();
                data.put(key, occupied, d);
            }
            return d;
        }
        
        public B set(E key, OccupiedBases occupiedBases, Advances advances, int count) {
            getMultiset(key, occupiedBases).setCount(advances, count);
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
    
    
    private static class Storage {
        private static final String KEY = "Key";
        private static final String ENTRY = "Entry";
        private static final String BASES = "Bases";
        private static final String ADVANCES = "Advances";
        private static final String COUNT = "Count";
        
        public static <E extends AdvanceDistributionKey> void store(AdvanceDistribution<E> d, Persister p) {
            for (E key : d.keySet()) {
                Persister keyPersister = p.newChild(KEY);
                key.store(keyPersister);
                storeRow(d.forKey(key), keyPersister);
            }
        }
        
        private static void storeRow(ImmutableMap<OccupiedBases, ImmutableMultiset<Advances>> row, Persister p) {
            for (Map.Entry<OccupiedBases, ImmutableMultiset<Advances>> e : row.entrySet()) {
                Persister entryPersister = p.newChild(ENTRY).putString(BASES, e.getKey().name());
                for (Multiset.Entry<Advances> advances : e.getValue().entrySet()) {
                    Persister advancesPersister = entryPersister.newChild(ADVANCES)
                            .putInt(COUNT, advances.getCount());
                    advances.getElement().store(advancesPersister);
                }
            }
        }
        
        public static <E, B extends BuilderBase<E, B>> void restore(Persister p, B builder, Function<Persister, E> keyReader) {
            for (Persister keyPersister : p.getChildren(KEY)) {
                E key = keyReader.apply(keyPersister);
                restoreRows(key, keyPersister, builder);
            }
        }

        private static <E, B extends BuilderBase<E, B>> void restoreRows(E key, Persister p, B builder) {
            for (Persister entryPersister : p.getChildren(ENTRY)) {
                OccupiedBases occupiedBases = OccupiedBases.valueOf(entryPersister.getString(BASES));
                for (Persister advancesPersister : entryPersister.getChildren(ADVANCES)) {
                    int count = advancesPersister.getInt(COUNT);
                    Advances advances = Advances.restoreFrom(advancesPersister);
                    builder.set(key, occupiedBases, advances, count);
                }
            }
        }
    }
}
