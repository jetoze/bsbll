package bsbll.game.play;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.bases.BaseSituation;
import bsbll.game.BaseRunner;
import bsbll.game.PlayerGameStats;
import bsbll.player.Player;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

/**
 * The result of a batter's turn at bat. Contains all the individual plays that 
 * took place during the plate appearance.
 */
@Immutable
public final class AtBatResult { // TODO: Come up with a better name
    private final Player batter;
    private final Player pitcher;
    private final ImmutableList<PlayOutcome> plays;
    private final ImmutableList<BaseRunner> runs;
    private final BaseSituation newBaseSituation;
    private final boolean batterCompletedHisTurn;
    private final AtBatStats stats;

    public AtBatResult(Player batter, 
                       Player pitcher,
                       List<PlayOutcome> plays,
                       List<BaseRunner> runs,
                       BaseSituation newBaseSituation,
                       boolean batterCompletedHisTurn,
                       AtBatStats stats) {
        this.batter = requireNonNull(batter);
        this.pitcher = requireNonNull(pitcher);
        this.plays = ImmutableList.copyOf(plays);
        this.runs = ImmutableList.copyOf(runs);
        this.newBaseSituation = requireNonNull(newBaseSituation);
        this.batterCompletedHisTurn = batterCompletedHisTurn;
        this.stats = requireNonNull(stats);
    }

    public Player getBatter() {
        return batter;
    }

    public Player getPitcher() {
        return pitcher;
    }

    public ImmutableList<PlayOutcome> getPlays() {
        return plays;
    }
    
    public ImmutableList<BaseRunner> getRuns() {
        return runs;
    }
    
    public int getNumberOfRuns() {
        return runs.size();
    }

    public boolean isBaseHit() {
        // Only the very last play can represent a hit. This is the play that contains the
        // outcome of the batter-pitcher matchup.
        return plays.get(plays.size() - 1).isBaseHit();
    }

    public BaseSituation getNewBaseSituation() {
        return newBaseSituation;
    }

    public boolean didBatterCompleteHisTurn() {
        return batterCompletedHisTurn;
    }
    
    /**
     * Updates the PlayerGameStats with the individual stats from this at bat.
     * This includes stats for the batter and pitcher, as well as base running
     * events. Pitcher runs and earned runs are <em>not</em> included - they are
     * processed separately after the inning is over.
     */
    public void gatherPlayerStats(PlayerGameStats gameStats) {
        this.stats.applyTo(batter, pitcher, gameStats);
    }
    
    public static Builder builder(Player batter, Player pitcher) {
        return new Builder(batter, pitcher);
    }

    
    public static final class Builder {
        private final Player batter;
        private final Player pitcher;
        private final List<PlayOutcome> actualPlays = new ArrayList<>();
        private final List<BaseRunner> runs = new ArrayList<>();
        private BaseSituation newBaseSituation;
        private boolean batterCompletedHisTurn;
        private final AtBatStats.Builder statsBuilder = AtBatStats.builder();
        
        public Builder(Player batter, Player pitcher) {
            this.batter = requireNonNull(batter);
            this.pitcher = requireNonNull(pitcher);
        }
        
        public Builder addOutcome(PlayOutcome outcome) {
            requireNonNull(outcome);
            this.actualPlays.add(outcome);
            addPitchingStat(PrimitivePitchingStat.OUTS, outcome.getNumberOfOuts());
            return this;
        }
        
        public Builder withNewBaseSituation(BaseSituation bs) {
            this.newBaseSituation = requireNonNull(bs);
            return this;
        }
        
        public Builder addStat(PrimitiveBattingStat battingStat, PrimitivePitchingStat pitchingStat) {
            addBattingStat(battingStat);
            addPitchingStat(pitchingStat);
            return this;
        }
        
        public Builder addBattingStat(PrimitiveBattingStat stat) {
            return addBattingStat(stat, 1);
        }
        
        public Builder addBattingStat(PrimitiveBattingStat stat, int value) {
            statsBuilder.add(stat, value);
            return this;
        }
        
        public Builder addPitchingStat(PrimitivePitchingStat stat) {
            return addPitchingStat(stat, 1);
        }
        
        public Builder addPitchingStat(PrimitivePitchingStat stat, int value) {
            statsBuilder.add(stat, value);
            return this;
        }
        
        public Builder runsScored(List<BaseRunner> runs) {
            this.runs.addAll(runs);
            runs.stream()
                .map(BaseRunner::getRunner)
                .forEach(statsBuilder::scored);
            return this;
        }
        
        public Builder stoleBase(Player player) {
            statsBuilder.stoleBase(player);
            return this;
        }
        
        public Builder caughtStealing(Player player) {
            statsBuilder.caughtStealing(player);
            return this;
        }

        public Builder batterCompletedHisTurn() {
            addBattingStat(PrimitiveBattingStat.PLATE_APPEARANCES, 1);
            addPitchingStat(PrimitivePitchingStat.BATTERS_FACED, 1);
            this.batterCompletedHisTurn = true;
            return this;
        }
        
        public AtBatResult build() {
            checkState(actualPlays.size() > 0, "Must provide at least one PlayOutcome");
            checkState(newBaseSituation != null, "Must provide the new BaseSituation");
            return new AtBatResult(batter, pitcher, actualPlays, runs, 
                    newBaseSituation, batterCompletedHisTurn, statsBuilder.build());
        }
    }
}
