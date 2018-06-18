package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import bsbll.bases.BaseSituation;
import bsbll.game.RunsScored.Run;
import bsbll.game.event.GameEvent;
import bsbll.game.event.GameEventDetector;
import bsbll.game.play.AtBatResult;
import bsbll.game.play.Play;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.team.BattingOrder;

public final class HalfInning {
    private final Inning inning;
    private final BattingOrder battingOrder;
    private final Player pitcher;
    private final GamePlayDriver driver;
    private final PlayerGameStats playerStats; // TODO: do this via an observer instead?
    private final GameEventDetector eventDetector;
    private RunsNeededToWin runsNeededToWin;

    /**
     * 
     * @param inning
     *            what inning we're in
     * @param battingOrder
     *            the batting order
     * @param pitcher
     *            the pitcher
     * @param driver
     *            the game play driver, that control things like the batter-pitcher matchup and 
     *            base running in this HalfInning.
     * @param playerStats
     *            the {@code PlayerGameStats} instance that keeps track of the
     *            individual player performances in the game.
     * @param eventDetector
     *            the {@code GameEventDetector} that looks for
     *            {@code GameEvent}s to report in the box score.
     * @param runsNeededToWin
     *            if the bottom of ninth inning or later, the number of runs
     *            needed by the batting team to win the game. This half inning
     *            will come to a stop once this many runs score (or three outs
     *            are made). {@code RunsNeededToWin.notApplicable()} if not applicable.
     */
    public HalfInning(Inning inning,
                      BattingOrder battingOrder, 
                      Player pitcher, 
                      GamePlayDriver driver,
                      PlayerGameStats playerStats,
                      GameEventDetector eventDetector,
                      RunsNeededToWin runsNeededToWin) {
        this.inning = requireNonNull(inning);
        this.battingOrder = requireNonNull(battingOrder);
        this.pitcher = requireNonNull(pitcher);
        this.driver = requireNonNull(driver);
        this.playerStats = requireNonNull(playerStats);
        this.eventDetector = requireNonNull(eventDetector);
        this.runsNeededToWin = runsNeededToWin;
    }

    public Summary run() {
        Loop loop = new Loop();
        Summary summary = loop.run();
        return summary;
    }
    
    @Override
    public String toString() {
        return inning.toString();
    }
    
    private class Loop {
        private Stats stats = new Stats();
        private BaseSituation baseSituation = BaseSituation.empty();
        private final List<Play> plays = new ArrayList<>();
        private final List<Play> idealPlays = new ArrayList<>();
        private final List<Run> runs = new ArrayList<>();
        private final List<GameEvent> events = new ArrayList<>();

        public Summary run() {
            do {
                Player batter = battingOrder.nextBatter();
                AtBatResult result = driver.run(batter, pitcher, baseSituation, stats.outs, runsNeededToWin);
                for (PlayOutcome outcome : result.getActualPlays()) {
                    checkState(!isDone(stats));
                    processPlay(batter, outcome);
                }
                result.getIdealPlays().stream()
                    .map(po -> new Play(batter, pitcher, po))
                    .forEach(idealPlays::add);
                result.getRuns().stream()
                    .map(r -> new Run(inning, r))
                    .forEach(this.runs::add);
                
                runsNeededToWin = runsNeededToWin.updateWithRunsScored(result.getNumberOfRuns());
                
                stats = stats.plus(result);
                baseSituation = result.getNewBaseSituation();
                result.gatherPlayerStats(playerStats);
                if (!result.didBatterCompleteHisTurn()) {
                    battingOrder.returnBatter(batter);
                }
            } while (!isDone(stats));
            int lob = baseSituation.getNumberOfRunners();
            return new Summary(inning, stats.withLeftOnBase(lob), plays, idealPlays, runs, events);
        }
        
        private boolean isDone(Stats stats) {
            if (stats.getOuts() == 3) {
                return true;
            }
            if (stats.getOuts() > 3) {
                throw new RuntimeException("Invalid number of outs: " + stats.getOuts());
            }
            if (runsNeededToWin.isGameOver()) {
                return true;
            }
            return false;
        }
        
        private void processPlay(Player batter, PlayOutcome outcome) {
            // TODO: Once we implement pitcher substitutions, if FIELDERS_CHOICE we need to
            // update who's responsible for the batter on base. (The pitcher who was responsible 
            // for the runner that was out will now become responsible for the batter, even if
            // that pitcher has been taken out of the game by now.)
            plays.add(new Play(batter, pitcher, outcome));
            eventDetector.examine(outcome, inning, batter, pitcher, stats.outs, baseSituation).ifPresent(events::add);
        }
    }
    
    
    @Immutable
    public final static class Stats {
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
        
        private Stats withLeftOnBase(int lob) {
            return new Stats(runs, hits, errors, outs, lob);
        }
        
        private Stats plus(AtBatResult abr) {
            int r = this.runs;
            int e = this.errors;
            int o = this.outs;
            for (PlayOutcome p : abr.getActualPlays()) {
                r += p.getNumberOfRuns();
                e += p.getNumberOfErrors();
                o += p.getNumberOfOuts();
            }
            int h = this.hits + (abr.isBaseHit() ? 1 : 0);
            int lob = 0; // Calculated at the end of the inning, not after individual at bats.
            return new HalfInning.Stats(r, h, e, o, lob);
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
    
    
    @Immutable
    public static final class Summary {
        private final Inning inning;
        private final Stats stats;
        private final ImmutableList<Play> plays;
        private final ImmutableList<Play> idealPlays;
        private final ImmutableList<Run> runs;
        private final ImmutableList<GameEvent> events;
        
        public Summary(Inning inning, Stats stats, List<Play> plays, List<Play> idealPlays, List<Run> runs, List<GameEvent> events) {
            this.inning = requireNonNull(inning);
            this.stats = requireNonNull(stats);
            this.plays = ImmutableList.copyOf(plays);
            this.idealPlays = ImmutableList.copyOf(idealPlays);
            this.runs = ImmutableList.copyOf(runs);
            this.events = ImmutableList.copyOf(events);
            checkArgument(runs.size() == stats.runs);
        }
        
        public Inning getInning() {
            return inning;
        }

        public Stats getStats() {
            return stats;
        }

        public ImmutableList<Play> getPlays() {
            return plays;
        }
        
        ImmutableList<Play> getIdealPlays() {
            return idealPlays;
        }

        public ImmutableList<Run> getRuns() {
            return runs;
        }
        
        public ImmutableList<GameEvent> getEvents() {
            return events;
        }
        
        /**
         * Checks if the earned run designation requires an ideal version of this inning to
         * be reconstructed, due to runs being scored after errors or passed balls.
         */
        boolean isEarnedRunReconstructionNeeded() {
            if (runs.isEmpty()) {
                return false;
            }
            // Reconstruction is necessary if at least one run scored *after* an error or passed ball
            boolean errorOrPassedBallHasHappened = false;
            for (Play p : plays) {
                if (p.isErrorOrPassedBall()) {
                    errorOrPassedBallHasHappened = true;
                }
                if (p.getNumberOfRuns() > 0 && errorOrPassedBallHasHappened) {
                    return true;
                }
            }
            return false;
        }
    }
}
