package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.RunsScored.Run;
import bsbll.game.event.GameEvent;
import bsbll.game.event.GameEventDetector;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;
import bsbll.team.BattingOrder;
import tzeth.collections.ImCollectors;

public final class HalfInning {
    private final Inning inning;
    private final BattingOrder battingOrder;
    private final Player pitcher;
    private final GamePlayDriver driver;
    private final PlayerGameStats playerStats; // TODO: do this via an observer instead?
    private final GameEventDetector eventDetector;
    private final int runsNeededToWin;
    private final Map<Player, Player> runnerToResponsiblePitcher = new HashMap<>();

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
     *            are made). {@code 0} if not applicable.
     */
    public HalfInning(Inning inning,
                      BattingOrder battingOrder, 
                      Player pitcher, 
                      GamePlayDriver driver,
                      PlayerGameStats playerStats,
                      GameEventDetector eventDetector,
                      int runsNeededToWin) {
        this.inning = requireNonNull(inning);
        this.battingOrder = requireNonNull(battingOrder);
        this.pitcher = requireNonNull(pitcher);
        this.driver = requireNonNull(driver);
        this.playerStats = requireNonNull(playerStats);
        this.eventDetector = requireNonNull(eventDetector);
        this.runsNeededToWin = runsNeededToWin;
    }

    public Summary run() {
        Stats stats = new Stats();
        List<Run> runs = new ArrayList<>();
        List<GameEvent> events = new ArrayList<>();
        BaseSituation baseSituation = BaseSituation.empty();
        do {
            Player batter = battingOrder.nextBatter();
            runnerToResponsiblePitcher.put(batter, pitcher);
            // TODO: Call driver.preMatchupCompletionPlays
            PlayOutcome outcome = driver.runMatchup(batter, pitcher, baseSituation, stats.outs);
            eventDetector.examine(outcome, inning, batter, pitcher, stats.outs, baseSituation).ifPresent(events::add);
            StateAfterMatchup sam = evaluateOutcome(batter, baseSituation, outcome, stats);
            stats = sam.stats;
            baseSituation = sam.baseSituation;
            runs.addAll(sam.runs);
            playerStats.update(batter, pitcher, outcome, sam.playersThatScored());
        } while (!isDone(stats));
        int lob = baseSituation.getNumberOfRunners();
        return new Summary(stats.withLeftOnBase(lob), runs, events);
    }
    
    private StateAfterMatchup evaluateOutcome(Player batter, BaseSituation baseSituation, PlayOutcome outcome, Stats preStats) {
        ResultOfAdvance roa = baseSituation.advanceRunners(batter, outcome.getAdvances());
        Stats newStats = new Stats(
                preStats.runs + roa.getNumberOfRuns(),
                preStats.hits + (outcome.isBaseHit() ? 1 : 0),
                preStats.errors + outcome.getNumberOfErrors(),
                preStats.outs + outcome.getNumberOfOuts(), // FIXME: I can add up to > 3 at the moment
                preStats.leftOnBase);
        ImmutableList<Run> runs = roa.getRunnersThatScored().stream()
                .map(p -> new Run(inning, p, runnerToResponsiblePitcher.get(p)))
                .collect(ImCollectors.toList());
        return new StateAfterMatchup(newStats, runs, roa.getNewSituation());
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
        public final ImmutableList<Run> runs;
        public final BaseSituation baseSituation;
        
        public StateAfterMatchup(Stats stats, ImmutableList<Run> runs, 
                BaseSituation baseSituation) {
            this.stats = stats;
            this.runs = runs;
            this.baseSituation = baseSituation;
        }
        
        public List<Player> playersThatScored() {
            return runs.stream()
                    .map(Run::getRunner)
                    .collect(toList());
        }
    }
    
    
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
    
    
    public static final class Summary {
        private final Stats stats;
        private final ImmutableList<Run> runs;
        private final ImmutableList<GameEvent> events;
        
        public Summary(Stats stats, List<Run> runs, List<GameEvent> events) {
            this.stats = stats;
            this.runs = ImmutableList.copyOf(runs);
            this.events = ImmutableList.copyOf(events);
            checkArgument(runs.size() == stats.runs);
        }

        public Stats getStats() {
            return stats;
        }

        public ImmutableList<Run> getRuns() {
            return runs;
        }
        
        public ImmutableList<GameEvent> getEvents() {
            return events;
        }
    }
}
