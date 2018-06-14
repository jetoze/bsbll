package bsbll.bases;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import tzeth.collections.ImCollectors;

/**
 * Represents all base advances made on a single play.
 * <p>
 * This class implements {@code Iterable<Advance>}. The iteration order is
 * sorted by originating base, in descending order. That is, an advance from
 * third base appears before an advance from second base, and so on.
 */
@Immutable
public final class Advances implements Iterable<Advance> {
    /**
     * Maps each Advance to the originating Base. Sorted in descending order,
     * e.g. "3-H;1-3;B-1".
     */
    private final ImmutableSortedMap<Base, Advance> advances;

    public static Advances of(Advance... individualAdvances) {
        return new Advances(Arrays.asList(individualAdvances));
    }
    
    /**
     * Creates an {@code Advances} instance from a previous base situation for
     * the case where the batter is awarded first base, e.g. via a walk or hit
     * by pitch. The batter advances to first, runners that are forced advance
     * accordingly.
     */
    public static Advances batterAwardedFirstBase(Set<Base> occupiedBases) {
        checkArgument(occupiedBases.stream().allMatch(Base::isOccupiable));
        List<Advance> advances = new ArrayList<>();
        advances.add(Advance.safe(Base.HOME, Base.FIRST));
        for (Base b : Base.occupiable()) {
            if (occupiedBases.contains(b)) {
                advances.add(Advance.safe(b, b.next()));
            } else {
                break;
            }
        }
        return new Advances(advances);
    }
    
    public Advances(Collection<Advance> individualAdvances) {
        checkLegal(individualAdvances);
        Map<Base, Advance> map = individualAdvances.stream()
                .collect(Collectors.toMap(Advance::from, a -> a));
        Comparator<Base> order = Base.comparingOrigin();
        this.advances = ImmutableSortedMap.<Base, Advance>orderedBy(order).putAll(map).build();
    }
    
    private static void checkLegal(Collection<Advance> advances) {
        // TODO: Can this be done more elegantly?
        Set<Base> safeAt = new HashSet<>();
        for (Advance a : advances) {
            if (a.isSafe()) {
                Base to = a.to();
                if (to.isOccupiable() && !safeAt.add(a.to())) {
                    throw new InvalidBaseSitutationException("More than one runner cannot advance safely to " + to);
                }
            }
        }
        
        Set<Base> from = new HashSet<>();
        for(Advance a : advances) {
            if (!from.add(a.from())) {
                throw new InvalidBaseSitutationException("More than one runner cannot advance from " + a.from());
            }
        }
    }
    
    @Override
    public Iterator<Advance> iterator() {
        return this.advances.values().iterator();
    }

    public int getNumberOfRuns() {
        return count(Advance::isRun);
    }
    
    private int count(Predicate<? super Advance> filter) {
        return (int) this.advances.values().stream()
                .filter(filter)
                .count();
    }
    
    public int getNumberOfOuts() {
        return count(Advance::isOut);
    }
    
    public boolean isBatterIncluded() {
        return this.advances.containsKey(Base.HOME);
    }
    
    public boolean contains(Base base) {
        requireNonNull(base);
        return this.advances.containsKey(base);
    }
    
    public boolean contains(Advance a) {
        return a.equals(this.advances.get(a.from()));
    }
    
    public boolean isNotKnown(Base base) {
        return !contains(base);
    }
    
    public Advance getAdvanceFrom(Base base) {
        checkArgument(contains(base));
        return this.advances.get(base);
    }

    public boolean didRunnerAdvanceSafely(Base base) {
        requireNonNull(base);
        Advance a = this.advances.get(base);
        return (a != null) && a.isAdvancement();
    }
    
    public boolean didRunnerScore(Base base) {
        if (!contains(base)) {
            return false;
        }
        Advance a = getAdvanceFrom(base);
        return a.isRun();
    }

    /**
     * Returns a stream of the runners that scored, in the order they scored in.
     */
    public Stream<Advance> getRunnersThatScored() {
        return this.advances.values().stream().filter(Advance::isRun);
    }
    
    public boolean didRunnerAdvance(Base base) {
        requireNonNull(base);
        return this.advances.containsKey(base);
    }

    public Advances concat(Advance a) {
        requireNonNull(a);
        List<Advance> list = new ArrayList<>(this.advances.values());
        list.add(a);
        return new Advances(list);
    }
    
    public Stream<Advance> stream(Predicate<Advance> filter) {
        return this.advances.values().stream().filter(filter);
    }
    
    public ImmutableList<Advance> collect(Predicate<Advance> filter) {
        return stream(filter).collect(ImCollectors.toList());
    }

    public Advances replace(Advance a) {
        checkArgument(this.advances.containsKey(a.from()));
        Map<Base, Advance> tmp = new HashMap<>(this.advances);
        tmp.put(a.from(), a);
        return new Advances(tmp.values());
    }
    
    ImmutableMap<Base, Base> toMap() {
        return this.advances.values().stream()
                .collect(ImCollectors.toMap(Advance::from, Advance::to));
    }
    
    @Override
    public int hashCode() {
        return this.advances.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj == this) || ((obj instanceof Advances) && 
                this.advances.equals(((Advances) obj).advances));
    }

    @Override
    public String toString() {
        return this.advances.values().toString();
    }
    
}
