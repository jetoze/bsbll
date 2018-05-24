package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.Objects;

import javax.annotation.Nullable;

import bsbll.card.season.PlayerCardLookup;
import bsbll.game.BaseSituation.ResultOfAdvance;
import bsbll.matchup.Log5BasedMatchupRunner.Outcome;
import bsbll.matchup.MatchupRunner;
import bsbll.player.Player;
import bsbll.team.Lineup;

public final class HalfInning {
    private final Lineup batting;
    private final Lineup fielding;
    private final PlayerCardLookup playerCardLookup;
    private final MatchupRunner matchupRunner;
    private final int runsNeededToWin;

    /**
     * 
     * @param batting
     *            the batting team
     * @param fielding
     *            the fielding/pitching team
     * @param matchupRunner
     *            the MatchupRunner that will be asked to simulate the matchup
     *            between the pitcher and the batters in this half inning.
     * @param runsNeededToWin
     *            if the bottom of ninth inning or later, the number of runs
     *            needed by the batting team to win the game. This half inning
     *            will come to a stop once this many runs score (or three outs
     *            are made). {@code 0} if not applicable.
     */
    public HalfInning(Lineup batting, 
                      Lineup fielding, 
                      MatchupRunner matchupRunner,
                      PlayerCardLookup playerCardLookup,
                      int runsNeededToWin) {
        this.batting = requireNonNull(batting);
        this.fielding = requireNonNull(fielding);
        this.playerCardLookup = requireNonNull(playerCardLookup);
        this.matchupRunner = requireNonNull(matchupRunner);
        this.runsNeededToWin = runsNeededToWin;
    }

    // TODO: Let this method return a structure containing hits, runs, and errors, instead of 
    // having getters for them.
    public Stats run() {
        Stats stats = new Stats();
        BaseSituation baseSituation = BaseSituation.empty();
        do {
            Player batter = batting.nextBatter();
            Player pitcher = fielding.getPitcher();
            Outcome outcome = matchupRunner.run(
                    playerCardLookup.getBattingCard(batter), 
                    playerCardLookup.getPitchingCard(pitcher));
            StateAfterMatchup sam = evaluateOutcome(batter, baseSituation, outcome, stats);
            stats = sam.stats;
            baseSituation = sam.baseSituation;
        } while (!isDone(stats));
        int lob = baseSituation.getNumberOfRunners();
        return stats.withLeftOnBase(lob);
    }
    
    private StateAfterMatchup evaluateOutcome(Player batter, BaseSituation baseSituation, Outcome outcome, Stats preStats) {
        if (outcome.isOut()) {
            // TODO: This is just to get things up and running. Eventually we will have to do 
            // things like:
            //   + Evaluate sacrifice hits / flies
            //   + Evaluate errors
            //   + Evaluate fielder's choice
            //   + Evaluate runners advancing
            //   + Etc?
            return new StateAfterMatchup(preStats.addOut(), baseSituation);
        }
        ResultOfAdvance roa = baseSituation.advanceRunners(batter, outcome);
        Stats newStats = new Stats(
                preStats.runs + roa.getNumberOfRuns(),
                preStats.hits + (outcome.isHit() ? 1 : 0),
                preStats.errors,
                preStats.outs,
                preStats.leftOnBase);
        return new StateAfterMatchup(newStats, roa.getNewSituation());
    }
    
    private boolean isDone(Stats stats) {
        if (stats.getOuts() == 3) {
            return true;
        }
        if (stats.getOuts() > 3) {
            throw new RuntimeException("Invalid number of outs: " + stats.getOuts());
        }
        if ((runsNeededToWin > 0) && (stats.getRuns() >= runsNeededToWin)) {
            return true;
        }
        return false;
    }
    
    
    private static class StateAfterMatchup {
        public final Stats stats;
        public final BaseSituation baseSituation;
        
        public StateAfterMatchup(Stats stats, BaseSituation baseSituation) {
            this.stats = stats;
            this.baseSituation = baseSituation;
        }
    }
    
    
    public final class Stats {
        private final int runs;
        private final int hits;
        private final int errors;
        private final int outs;
        private final int leftOnBase;
        
        public Stats() {
            this(0, 0, 0, 0, 0);
        }
        
        // XXX: Ugly ctor, with all these ints. Mostly (only?) for internal use, but still.
        public Stats(int runs, int hits, int errors, int outs, int leftOnBase) {
            this.runs = checkNotNegative(runs);
            this.hits = checkNotNegative(hits);
            this.errors = checkNotNegative(errors);
            this.outs = checkNotNegative(outs);
            this.leftOnBase = checkNotNegative(leftOnBase);
        }

        public int getRuns() {
            return runs;
        }

        public int getHits() {
            return hits;
        }

        public int getErrors() {
            return errors;
        }

        public int getOuts() {
            return outs;
        }

        public int getLeftOnBase() {
            return leftOnBase;
        }
        
        public Stats addOut() {
            return new Stats(runs, hits, errors, outs + 1, leftOnBase);
        }
        
        public Stats withLeftOnBase(int lob) {
            return new Stats(runs, hits, errors, outs, lob);
        }
        
        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Stats) {
                Stats that = (Stats) obj;
                return (this.runs == that.runs) && (this.hits == that.hits) &&
                        (this.errors == that.errors) && (this.outs == that.outs) &&
                        (this.leftOnBase == that.leftOnBase);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(runs, hits, errors, outs, leftOnBase);
        }
        
        @Override
        public String toString() {
            return String.format("R: %d, H: %d, E: %d, O: %d, LOB: %d", runs, hits, errors, outs, leftOnBase);
        }
    }
}
