package bsbll.game.play;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
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
import bsbll.player.Player;

@Immutable
public final class PlayOutcome {
    private final EventType type;    
    private final Advances advances;
    private final int numberOfErrors;
    @Nullable
    private final PlayOutcome ideal;

    public PlayOutcome(EventType type, 
            Advances advances) {
        this(type, advances, 0);
    }
    
    public PlayOutcome(EventType type, 
                       Advances advances,
                       int numberOfErrors) {
        this(type, advances, numberOfErrors, null);
    }
    
    public PlayOutcome(EventType type, 
                       Advances advances,
                       int numberOfErrors,
                       @Nullable PlayOutcome idealOutcome) {
        this.type = requireNonNull(type);
        this.advances = requireNonNull(advances);
        this.numberOfErrors = checkNotNegative(numberOfErrors);
        this.ideal = idealOutcome;
        checkArgument(idealOutcome == null || numberOfErrors > 0, 
                "An ideal outcome should only be provided when there are errors on the play.");
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
    
    public BaseSituation applyTo(Player batter, BaseSituation baseSituation) {
        return baseSituation.advanceRunners(batter, this.advances).getNewSituation();
    }
    
    public List<Player> getScoringPlayers(Player batter, BaseSituation baseSituation) {
        return baseSituation.advanceRunners(batter, this.advances).getRunnersThatScored();
    }
    
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }
    
    // TODO: I need a better name
    // TODO: Is this the best way of allowing the official scorer reconstruct the inning without errors?
    /**
     * Returns the ideal version of this outcome, i.e. how it would have been
     * had there not been errors on the play.
     * <p>
     * If this outcome did not have any errors, this method simply returns
     * {@code this}. Otherwise it returns the ideal outcome that was provided in
     * the {@link #PlayOutcome(EventType, Advances, int, PlayOutcome)
     * constructor} or via the {@link #withIdealOutcome(PlayOutcome) factory
     * method}.
     */
    public PlayOutcome getIdealOutcome() {
        if (this.numberOfErrors == 0) {
            return this;
        }
        checkState(this.ideal != null, "The ideal outcome has not been provided");
        return this.ideal;
    }

    /**
     * Returns a version of this PlayOutcome with the given ideal outcome. Should only be used if
     * this PlayOutcome has errors.
     */
    public PlayOutcome withIdealOutcome(PlayOutcome ideal) {
        checkState(this.numberOfErrors > 0, "An ideal outcome should only be given when there are errors on the play.");
        requireNonNull(ideal);
        return (ideal == this.ideal)
                ? this
                : new PlayOutcome(this.type, this.advances, this.numberOfErrors, ideal);
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
        @Nullable
        private PlayOutcome ideal;
        
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
        
        public Builder withIdealOutcome(PlayOutcome ideal) {
            this.ideal = requireNonNull(ideal);
            return this;
        }
        
        public PlayOutcome build() {
            return new PlayOutcome(this.type, new Advances(this.advances.build()), this.errors, this.ideal);
        }
    }
    
}
