package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import bsbll.card.season.PlayerCardLookup;
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
    public HalfInning(GameContext gameContext, 
                      Lineup batting, 
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
    public void run() {
        Stats stats = new Stats();
        BaseSituation baseSituation = BaseSituation.empty();
        do {
            Player batter = batting.nextBatter();
            Player pitcher = fielding.getPitcher();
            Outcome outcome = matchupRunner.run(
                    playerCardLookup.getBattingCard(batter), 
                    playerCardLookup.getPitchingCard(pitcher));
            stats = evaluateOutcome(baseSituation, outcome, stats);
        } while (!isDone(stats));
    }
    
    private Stats evaluateOutcome(BaseSituation baseSituation, Outcome outcome, Stats preStats) {
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
    }
}
