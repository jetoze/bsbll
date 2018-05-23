package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import bsbll.card.DieFactory;
import bsbll.matchup.Matchup;
import bsbll.matchup.Matchup.Outcome;
import bsbll.player.Player;
import bsbll.team.Lineup;

public final class HalfInning {
    private final Lineup batting;
    private final Lineup fielding;
    private final MatchupFactory matchupFactory;
    private final DieFactory dieFactory;
    private final int runsNeededToWin;

    /**
     * 
     * @param batting
     *            the batting team
     * @param fielding
     *            the fielding/pitching team
     * @param runsNeededToWin
     *            if the bottom of ninth inning or later, the number of runs
     *            needed by the batting team to win the game. This half inning
     *            will come to a stop once this many runs score (or three outs
     *            are made). {@code 0} if not applicable.
     */
    public HalfInning(GameContext gameContext, 
                      Lineup batting, 
                      Lineup fielding, 
                      MatchupFactory matchupFactory,
                      DieFactory dieFactory,
                      int runsNeededToWin) {
        this.batting = requireNonNull(batting);
        this.fielding = requireNonNull(fielding);
        this.matchupFactory = requireNonNull(matchupFactory);
        this.dieFactory = requireNonNull(dieFactory);
        this.runsNeededToWin = runsNeededToWin;
    }

    // TODO: Let this method return a structure containing hits, runs, and errors, instead of 
    // having getters for them.
    public void run() {
        Stats stats = new Stats(0, 0, 0, 0);
        do {
            Player batter = batting.nextBatter();
            Player pitcher = fielding.getPitcher();
            Matchup matchup = matchupFactory.createMatchup(batter, pitcher);
            Outcome outcome = matchup.run(dieFactory);
            stats = evaluateOutcome(outcome, stats);
        } while (!isDone(stats));
    }
    
    private Stats evaluateOutcome(Outcome outcome, Stats preStats) {
        // TODO: Implement me.
        return null;
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
    
    
    public final class Stats {
        private final int runs;
        private final int hits;
        private final int errors;
        private final int outs;
        
        // XXX: Ugly ctor, with four ints. Mostly (only?) for internal use, but still.
        public Stats(int runs, int hits, int errors, int outs) {
            this.runs = checkNotNegative(runs);
            this.hits = checkNotNegative(hits);
            this.errors = checkNotNegative(errors);
            this.outs = checkNotNegative(outs);
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
    }

}
