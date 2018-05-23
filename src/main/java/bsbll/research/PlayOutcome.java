package bsbll.research;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import bsbll.Base;
import bsbll.game.BaseSituation;
import bsbll.player.Player;

public final class PlayOutcome {
    private final EventType type;    
    private final Advances advances;
    private final int numberOfErrors;
    
    public PlayOutcome(EventType type, 
                       Advances advances,
                       int numberOfErrors) {
        this.type = requireNonNull(type);
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
        int outs = this.advances.getNumberOfOuts();
        if (this.type.isBatterOut() && !this.advances.isBatterIncluded()) {
            ++outs;
        }
        return outs;
    }
    
    public int getNumberOfRuns() {
        return this.advances.getNumberOfRuns();
    }
    
    public BaseSituation applyTo(Player batter, BaseSituation baseSituation) {
        return this.advances.applyTo(batter, baseSituation);
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return this.advances.getScoringPlayers(batter, baseSituation);
    }
    
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.numberOfErrors, this.advances);
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
                    this.advances.equals(that.advances);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s - Advances: %s Errors: %d", 
                this.type, this.advances, this.numberOfErrors);
    }
    
    public static Builder builder(EventType type) {
        return new Builder(type);
    }
    
    
    public static final class Builder {
        private final EventType type;
        private final ImmutableSet.Builder<Advance> advances = ImmutableSet.builder();
        private int errors;
        
        public Builder(EventType type) {
            this.type = requireNonNull(type);
            if (type.isError()) {
                this.errors = 1;
            }
        }
        
        public Builder withSafeAdvance(Base from, Base to) {
            return withAdvance(Advance.safe(from, to));
        }
        
        public Builder withAdvance(Advance adv) {
            this.advances.add(adv);
            return this;
        }
        
        public Builder withOut(Base from, Base to) {
            return withAdvance(Advance.out(from, to));
        }
        
        public Builder withErrors(int errors) {
            checkPositive(errors);
            this.errors += errors;
            return this;
        }
        
        public PlayOutcome build() {
            return new PlayOutcome(this.type, new Advances(this.advances.build()), this.errors);
        }
    }
    
}
