package bsbll.research;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class EventOutcome {
    private final EventType type;    
    private final ImmutableSet<Base> outs;
    private final ImmutableMap<Base, Base> advances;
    
    public EventOutcome(EventType type, 
                        Set<Base> outs,
                        Collection<Advance> advances) {
        this.type = requireNonNull(type);
        this.outs = ImmutableSet.copyOf(outs);
        // TODO: Add ImCollectors.toMap
        this.advances = ImmutableMap.copyOf(advances.stream()
                .collect(toMap(Advance::from, Advance::to)));
        checkAdvances();
    }
    
    private void checkAdvances() {
        // TODO: Check that we don't have two or more advances to the same base
        // other than HOME
    }

    public EventType getType() {
        return this.type;
    }
    
    public int getNumberOfOuts() {
        return this.outs.size();
    }
    
    public int getNumberOfRuns() {
        return (int) this.advances.values().stream()
                .filter(Base::isHome)
                .count();
    }
    
    // TODO: Add method that takes a "Base Situation" object as input, and returns a new,
    // resulting "Base Situation" object. "Base Situation" == what bases are currently occupied,
    // and by whom.
}
