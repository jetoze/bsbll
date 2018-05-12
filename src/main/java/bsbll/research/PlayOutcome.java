package bsbll.research;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

public final class PlayOutcome {
    private final EventType type;    
    private final Advances advances;
    private final ImmutableSet<Base> outs;
    private final int numberOfErrors;
    
    public PlayOutcome(EventType type, 
                       Advances advances,
                       Set<Base> outs,
                       int numberOfErrors) {
        this.type = requireNonNull(type);
        this.outs = ImmutableSet.copyOf(outs);
        this.advances = requireNonNull(advances);
        this.numberOfErrors = checkNotNegative(numberOfErrors);
    }

    public EventType getType() {
        return this.type;
    }

    public Advances getAdvances() {
        return this.advances;
    }
    
    public int getNumberOfOuts() {
        return this.outs.size();
    }
    
    public ImmutableSet<Base> getOuts() {
        return outs;
    }
    
    public int getNumberOfRuns() {
        return this.advances.getNumberOfRuns();
    }
    
    public BaseSituation advanceRunners(Player batter, BaseSituation baseSituation) {
        return this.advances.advanceRunners(batter, baseSituation);
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return this.advances.getScoringPlayers(batter, baseSituation);
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
    
}
