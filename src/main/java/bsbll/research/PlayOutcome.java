package bsbll.research;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import tzeth.collections.ImCollectors;

public final class PlayOutcome {
    private final EventType type;    
    private final ImmutableSet<Base> outs;
    private final ImmutableMap<Base, Base> advances;
    private final int numberOfErrors;
    
    public PlayOutcome(EventType type, 
                       Set<Base> outs,
                       Collection<Advance> advances,
                       int numberOfErrors) {
        this.type = requireNonNull(type);
        this.outs = ImmutableSet.copyOf(outs);
        this.advances = advances.stream()
                .collect(ImCollectors.toMap(Advance::from, Advance::to));
        this.numberOfErrors = checkNotNegative(numberOfErrors);
        checkAdvances();
    }
    
    private void checkAdvances() {
        // TODO: Can this be done more elegantly?
        Set<Base> tos = new HashSet<Base>();
        for (Base to : this.advances.values()) {
            if (!tos.add(to)) {
                if (to == Base.HOME) {
                    // This is OK - more than one runner can reach home on a play
                } else {
                    throw new IllegalArgumentException("More than one runner cannot advance to " + to);
                }
            }
        }
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
    
    public BaseSituation advanceRunners(Player batter, BaseSituation baseSituation) {
        return baseSituation.apply(batter, this.advances);
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return baseSituation.getScoringPlayers(batter, this.advances);
    }
    
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.numberOfErrors, this.advances, this.outs);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PlayOutcome) {
            PlayOutcome that = (PlayOutcome) obj;
            return (this.type == that.type) &&
                    (this.numberOfErrors == that.numberOfErrors) &&
                    this.outs.equals(that.outs) &&
                    this.advances.equals(that.advances);
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    
    public static final class Builder {
        private EventType type;
        private final ImmutableSet.Builder<Base> outs = ImmutableSet.builder();
        private final ImmutableSet.Builder<Advance> advances = ImmutableSet.builder();
        private int errors;
        
        public Builder withType(EventType type) {
            this.type = requireNonNull(type);
            if (type.isError()) {
                ++errors;
            }
            return this;
        }
        
        public Builder withOut(Base base) {
            this.outs.add(base);
            return this;
        }
        
        public Builder withAdvance(Advance adv) {
            this.advances.add(adv);
            return this;
        }
        
        public Builder withError() {
            this.errors++;
            return this;
        }
        
        public PlayOutcome build() {
            return new PlayOutcome(this.type, this.outs.build(), this.advances.build(), this.errors);
        }
    }
    
}
