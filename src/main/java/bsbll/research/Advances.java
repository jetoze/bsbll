package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSortedMap;

import tzeth.collections.ImCollectors;

/**
 * Represents all base advances made on a single play. 
 */
public final class Advances {
    /**
     * Sorted in descending order, e.g. "3-H;1-3;B-1". This is just for the benefit
     * of toString(), at least at the moment.
     */
    private final ImmutableSortedMap<Base, Base> advances;

    public Advances(Collection<Advance> individualAdvances) {
        this(individualAdvances.stream()
                .collect(ImCollectors.toMap(Advance::from, Advance::to)));
    }
    
    public Advances(Map<Base, Base> advances) {
        checkLegal(advances);
        this.advances = ImmutableSortedMap.<Base, Base>reverseOrder()
                .putAll(advances)
                .build();
    }
    
    private static void checkLegal(Map<Base, Base> advances) {
        // TODO: Can this be done more elegantly?
        Set<Base> tos = new HashSet<Base>();
        for (Base to : advances.values()) {
            if (!tos.add(to)) {
                checkArgument(to == Base.HOME, "More than one runner cannot advance to " + to);
            }
        }
        for (Map.Entry<Base, Base> e : advances.entrySet()) {
            checkArgument(e.getValue().compareTo(e.getKey()) > 0 || e.getKey() == Base.HOME,
                    "%s -> %s is not a valid advance", e.getKey(), e.getValue());
        }
    }
    
    public int getNumberOfRuns() {
        return (int) this.advances.values().stream()
                .filter(Base::isHome)
                .count();
    }
    
    public BaseSituation advanceRunners(Player batter, BaseSituation baseSituation) {
        return baseSituation.apply(batter, this.advances);
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return baseSituation.getScoringPlayers(batter, this.advances);
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
        return this.advances.toString();
    }

}
