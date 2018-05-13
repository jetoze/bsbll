package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSortedMap;

/**
 * Represents all base advances made on a single play. 
 */
public final class Advances {
    /**
     * Maps each Advance to the originating Base. Sorted in descending order,
     * e.g. "3-H;1-3;B-1".
     */
    private final ImmutableSortedMap<Base, Advance> advances;

    public static Advances of(Advance... individualAdvances) {
        return new Advances(Arrays.asList(individualAdvances));
    }
    
    public Advances(Collection<Advance> individualAdvances) {
        checkLegal(individualAdvances);
        Map<Base, Advance> map = individualAdvances.stream()
                .collect(toMap(Advance::from, a -> a));
        Comparator<Base> order = Comparator.comparing(Base::intValueWhenOrigin).reversed();
        this.advances = ImmutableSortedMap.<Base, Advance>orderedBy(order).putAll(map).build();
    }
    
    private static void checkLegal(Collection<Advance> advances) {
        // TODO: Can this be done more elegantly?
        Set<Base> safeAt = new HashSet<>();
        for (Advance a : advances) {
            if (a.isSafe()) {
                Base to = a.to();
                if (!safeAt.add(a.to())) {
                    checkArgument(to == Base.HOME, "More than one runner cannot advance safely to " + to);
                }
            }
        }
        
        Set<Base> from = new HashSet<>();
        for(Advance a : advances) {
            checkArgument(from.add(a.from()), "More than one runner cannot advance from " + a.from());
        }
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
    
    public BaseSituation applyTo(Player batter, BaseSituation situation) {
        requireNonNull(batter);
        requireNonNull(situation);
        if (this.advances.isEmpty()) {
            return situation;
        }
        Map<Base, Player> runners = situation.toMap();
        // The following takes advantage of the fact that we store the advances
        // in descending order of originating base.
        for (Advance a : advances.values()) {
            if (a.from() == Base.HOME) {
                // This is the batter. It is also guaranteed to be the last
                // advancement we process, since we do it in descending order.
                if (a.isSafe() && a.to().isOccupiable()) {
                    runners.put(a.to(), batter);
                }
            } else {
                Player runner = runners.get(a.from());
                checkArgument(runner != null, "The given BaseSituation is not applicable for "
                        + "advancement %s: there was no runner on %s.", a, a.from());
                if (a.isOut() || a.isAdvancement()) {
                    runners.remove(a.from());
                }
                if (a.isAdvancement() && !a.isRun()) {
                    runners.put(a.to(), runner);
                }
            }
        }
        return new BaseSituation(runners);
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return this.advances.values().stream()
                .filter(Advance::isRun)
                .map(Advance::from)
                .map(baseSituation::getRunner)
                .collect(toList());
    }
    
    public boolean didRunnerAdvance(Base base) {
        requireNonNull(base);
        return this.advances.containsKey(base);
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
