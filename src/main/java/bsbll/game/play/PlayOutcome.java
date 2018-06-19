package bsbll.game.play;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;
import static tzeth.preconds.MorePreconditions.checkPositive;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import bsbll.bases.Advance;
import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;
import bsbll.game.BaseRunner;

@Immutable
public final class PlayOutcome {
    private static final PlayOutcome NO_PLAY = new PlayOutcome(EventType.NO_PLAY, Advances.empty());
    private static final PlayOutcome STRIKE_OUT = new PlayOutcome(EventType.STRIKEOUT, Advances.empty());
    
    private final EventType type;    
    private final Advances advances;
    private final int numberOfErrors;

    public PlayOutcome(EventType type, 
            Advances advances) {
        this(type, advances, 0);
    }
    
    public PlayOutcome(EventType type, 
                       Advances advances,
                       int numberOfErrors) {
        this.type = requireNonNull(type);
        this.advances = requireNonNull(advances);
        this.numberOfErrors = checkNotNegative(numberOfErrors);
    }
    
    /**
     * Returns a PlayOutcome representing a No Play. This can be used e.g. to
     * represent the "ideal" play corresponding to a play like PASSED_BALL or
     * WILD_PITCH, when the official scorer reconstructs the inning in order to
     * figure out what runs are earned.
     */
    public static PlayOutcome noPlay() {
        return NO_PLAY;
    }
    
    /**
     * Returns a PlayOutcome representing a normal strikeout, where the batter
     * is out and nothing happens on the bases.
     */
    public static PlayOutcome strikeout() {
        return STRIKE_OUT;
    }

    public EventType getType() {
        return this.type;
    }
    
    public boolean isBaseHit() {
        return this.type.isHit();
    }
    
    public boolean isHomerun() {
        return this.type == EventType.HOMERUN;
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
    
    public BaseSituation applyTo(BaseRunner batter, BaseSituation baseSituation) {
        return baseSituation.advanceRunners(batter, this.advances).getNewSituation();
    }
    
    public List<BaseRunner> getScoringPlayers(BaseRunner batter, BaseSituation baseSituation) {
        return baseSituation.advanceRunners(batter, this.advances).getRunnersThatScored();
    }
    
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }
    
    public boolean isErrorOrPassedBall() { // TODO: Come up with a better name
        return this.numberOfErrors > 0 || this.type == EventType.PASSED_BALL;
    }
    
    public boolean isNoPlay() {
        return this.type == EventType.NO_PLAY;
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
        
        public Builder withSafeOnError(Base from, Base to) {
            return withAdvance(Advance.safeOnError(from, to));
        }
        
        public Builder withAdvance(Advance adv) {
            this.advances.add(adv);
            return this;
        }
        
        public Builder withOut(Base from, Base to) {
            return withAdvance(Advance.out(from, to));
        }
        
        /**
         * Sets the number of errors. This overrides any previous settings,
         * including an error implied from the event type.
         */
        public Builder withErrors(int errors) {
            checkNotNegative(errors);
            this.errors = errors;
            return this;
        }

        /**
         * Adds other errors than the one optionally implied by the event type.
         */
        public Builder withAdditionalErrors(int errors) {
            checkPositive(errors);
            this.errors += errors;
            return this;
        }
        
        public PlayOutcome build() {
            return new PlayOutcome(this.type, new Advances(this.advances.build()), this.errors);
        }
    }
    
}
