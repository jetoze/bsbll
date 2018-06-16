package bsbll.game.play;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;

/**
 * The result of a batter's turn at bat. Contains all the individual plays that 
 * took place during the plate appearance.
 */
@Immutable
public final class AtBatResult { // TODO: Come up with a better name
    private final Player batter;
    private final Player pitcher;
    private final ImmutableList<PlayOutcome> actualPlays;
    private final ImmutableList<PlayOutcome> idealPlays;
    private final boolean batterCompletedHisTurn;

    public AtBatResult(Player batter, 
                       Player pitcher,
                       List<PlayOutcome> actualPlays,
                       List<PlayOutcome> idealPlays,
                       boolean batterCompletedHisTurn) {
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
        this.actualPlays = ImmutableList.copyOf(actualPlays);
        this.idealPlays = ImmutableList.copyOf(idealPlays);
        this.batterCompletedHisTurn = batterCompletedHisTurn;
    }

    public Player getBatter() {
        return batter;
    }

    public Player getPitcher() {
        return pitcher;
    }

    public ImmutableList<PlayOutcome> getActualPlays() {
        return actualPlays;
    }

    public ImmutableList<PlayOutcome> getIdealPlays() {
        return idealPlays;
    }

    public boolean didBatterCompleteHisTurn() {
        return batterCompletedHisTurn;
    }
    
    public static Builder builder(Player batter, Player pitcher) {
        return new Builder(batter, pitcher);
    }
    
    
    public static final class Builder {
        private final Player batter;
        private final Player pitcher;
        private final List<PlayOutcome> actualPlays = new ArrayList<>();
        private final List<PlayOutcome> idealPlays = new ArrayList<>();
        private boolean batterCompletedHisTurn;
        
        public Builder(Player batter, Player pitcher) {
            this.batter = requireNonNull(batter);
            this.pitcher = requireNonNull(pitcher);
        }
        
        public Builder addOutcome(PlayOutcome outcome) {
            requireNonNull(outcome);
            this.actualPlays.add(outcome);
            return this;
        }
        
        public Builder addOutcome(PlayOutcome actual, PlayOutcome ideal) {
            requireNonNull(actual);
            requireNonNull(ideal);
            this.actualPlays.add(actual);
            this.idealPlays.add(ideal);
            return this;
        }
        
        public Builder batterCompletedHisTurn() {
            this.batterCompletedHisTurn = true;
            return this;
        }
        
        public AtBatResult build() {
            checkState(actualPlays.size() > 0, "Must provide at least one PlayOutcome");
            return new AtBatResult(batter, pitcher, actualPlays, idealPlays, batterCompletedHisTurn);
        }
    }
}