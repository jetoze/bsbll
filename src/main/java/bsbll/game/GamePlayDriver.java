package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkInRange;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.die.DieFactory;
import bsbll.game.params.BaseHitAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceKey;
import bsbll.game.params.ErrorCountDistribution;
import bsbll.game.params.FieldersChoiceProbabilities;
import bsbll.game.params.OutAdvanceDistribution;
import bsbll.game.params.OutAdvanceKey;
import bsbll.game.params.OutLocation;
import bsbll.game.play.AtBatResult;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.matchup.MatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;
import bsbll.stats.BattingStat;
import bsbll.stats.BattingStat.PrimitiveBattingStat;
import bsbll.stats.PitchingStat;
import bsbll.stats.PitchingStat.PrimitivePitchingStat;

/**
 * Generates the list of plays associated with each batter-pitcher matchup.
 * <p>
 * Some of the plays can occur before the matchup (plate appearance) completes,
 * such as a stolen base or a balk. They are returned by calling
 * {@link #preMatchupCompletionPlays(Player, Player, BaseSituation, int)
 * preMatchupCompletionPlays}. The play resulting from the matchup itself (hit,
 * strikeout, walk, etc), is then returned by calling
 * {@link #runMatchup(Player, Player, BaseSituation, int) runMatchup}.
 */
public final class GamePlayDriver {
    private final MatchupRunner matchupRunner;
    private final BaseHitAdvanceDistribution baseHitAdvanceDistribution;
    private final OutAdvanceDistribution outAdvanceDistribution;
    private final FieldersChoiceProbabilities fieldersChoiceProbabilities;
    private final ErrorCountDistribution errorCountDistribution;
    private final ErrorAdvanceDistribution errorAdvanceDistribution;
    private final DieFactory dieFactory;
    
    public GamePlayDriver(MatchupRunner matchupRunner,
                          BaseHitAdvanceDistribution baseHitAdvanceDistribution,
                          OutAdvanceDistribution outAdvanceDistribution,
                          FieldersChoiceProbabilities fieldersChoiceProbabilities,
                          ErrorCountDistribution errorCountDistribution,
                          ErrorAdvanceDistribution errorAdvanceDistribution,
                          DieFactory dieFactory) {
        this.matchupRunner = requireNonNull(matchupRunner);
        this.baseHitAdvanceDistribution = requireNonNull(baseHitAdvanceDistribution);
        this.outAdvanceDistribution = requireNonNull(outAdvanceDistribution);
        this.fieldersChoiceProbabilities = requireNonNull(fieldersChoiceProbabilities);
        this.errorCountDistribution = requireNonNull(errorCountDistribution);
        this.errorAdvanceDistribution = requireNonNull(errorAdvanceDistribution);
        this.dieFactory = requireNonNull(dieFactory);
    }
    
    // TODO: Use our own DieFactory in all calls to AdvanceDistribution etc.

    // TODO: In addition to just the batter and pitcher, a proper driver needs access to 
    // data about both teams lineups, e.g. to determine the probability that a runner on 
    // base will attempt to steal a base.
    // Two options:
    //    1. Create a new GamePlayDriver for each game, so that team and roster info can
    //       be passed in via the constructor;
    //    2. Use the same GamePlayDriver for all games of the league, and pass in the
    //       team and roster info in respective method call.
    
    // TODO: Instead of preMatchupCompletionPlays and runMatchup, we could let this class 
    // handle all of that, and just have a single method getPlays().
    // Pros: Simpler API.
    // Cons: This class needs more context about the current state (number of outs, 
    //       walk-off situations). We also need a way to communicate back to the caller 
    //       if the plate appearance was actually completed, or if the same batter should 
    //       be up again at the start of the next inning. On the other hand, knowledge about
    //       the current state would ideally be one of the deciding factors for how a play
    //       turns out anyway; the fielding team will behave differently in the bottom of
    //       the ninth inning of a tie game than in the bottom of the third being down 
    //       10 runs.
    // Follow-up: Go with the one-method approach, and return a Result object consisting of:
    //   + The batter
    //   + The pitcher
    //   + Flag that tells us if the batter completed his turn at bat
    //   + List of actual PlayOutcomes
    //   + List of ideal PlayOutcomes
    //   + ?
    
    // TODO: We pass in the same four parameters in both calls right now. Add a param class?
    
    // TODO: Depending on how we go about implementing earned run detection, we may also
    // want to return a list of "ideal" plays, i.e. how the matchup would have played out
    // in the absence of errors and passed balls. What's a good way of doing that? One way
    // that comes to mind is to add an "idealOutcome" property to PlayOutcome, i.e. ask the
    // PlayOutcome itself what the ideal version of it is. For normal outcomes, this will
    // simply be the PlayOutcome itself.
    
    public AtBatResult run(Player batter,
                           Player pitcher,
                           BaseSituation baseSituation,
                           int outs,
                           RunsNeededToWin runsNeededToWin) {
        AtBatDriver abDriver = new AtBatDriver(
                requireNonNull(batter),
                requireNonNull(pitcher),
                requireNonNull(baseSituation),
                checkInRange(outs, 0, 2),
                requireNonNull(runsNeededToWin));
        return abDriver.run();
    }
    
    
    
    private final class AtBatDriver {
        private final Player batter;
        private final Player pitcher;
        private BaseSituation baseSituation;
        private int outs;
        private RunsNeededToWin runsNeededToWin;
        
        private boolean batterShouldHaveBeenOut;
        
        private final AtBatResult.Builder builder;
        
        public AtBatDriver(Player batter, Player pitcher, BaseSituation baseSituation, int outs, RunsNeededToWin runsNeededToWin) {
            this.batter = batter;
            this.pitcher = pitcher;
            this.baseSituation = baseSituation;
            this.outs = outs;
            this.runsNeededToWin = runsNeededToWin;
            this.builder = AtBatResult.builder(batter, pitcher);
        }
        
        public AtBatResult run() {
            runPreMatchupPlays();
            if (!isDone()) {
                runMatchup();
            }
            return builder.withNewBaseSituation(baseSituation).build();
        }
        
        private boolean isDone() {
            return (outs >= 3) || runsNeededToWin.isGameOver();
        }
     
        /**
         * Simulates the plays, if any, that take place before the
         * batter-pitcher matchup completes. This includes things like stolen
         * base attempts, balks, or wild pitches.
         * <p>
         * Note that if any of the plays results in outs, we may reach the end
         * of the inning (three outs), in which case the batter-pitcher matchup
         * is terminated before the batter completes his turn at bat. Ditto if
         * any of these plays result in a walk-off run.
         */
        private void runPreMatchupPlays() {
            // TODO: Implement me
            // TODO: Remember to set the flag this.batterShouldHaveBeenOut to true
            // for events like ERROR_ON_FOUL_FLY.
            // TODO: Do we need a more general battersTimeAtBatShouldBeOver flag,
            // for the case where the ideal version of a pre-matchup play should
            // have resulted in the third out of the inning? For example, a 
            // CAUGHT_STEALING with an associated error, allowing the runner to 
            // remain safely on the bases? Can the official scorer assume that
            // such an event should normally have resulted in the runner being out?
        }
        
        /**
         * Runs the batter-pitcher matchup itself.
         */
        private void runMatchup() {
            Outcome basicOutcome = matchupRunner.run(batter, pitcher);
            processBasicMatchupOutcome(basicOutcome);
            builder.batterCompletedHisTurn();
        }
        
        private void processBasicMatchupOutcome(Outcome basicOutcome) {
            switch (basicOutcome) {
            case SINGLE:
                baseHit(BaseHit.SINGLE);
                break;
            case DOUBLE:
                baseHit(BaseHit.DOUBLE);
                break;
            case TRIPLE:
                baseHit(BaseHit.TRIPLE);
                break;
            case HOMERUN:
                baseHit(BaseHit.HOMERUN);
                break;
            case STRIKEOUT:
                // TODO: Dropped third strike could result in the batter going to first,
                // without an out being recorded.
                strikeout();
                break;
            case WALK:
                batterAwardedFirst(basicOutcome);
                break;
            case HIT_BY_PITCH:
                batterAwardedFirst(basicOutcome);
                break;
            case OUT:
                out();
                break;
            default:
                throw new AssertionError("Unexpected outcome: " + basicOutcome);
            }
        }
        
        private void baseHit(BaseHit baseHit) {
            if (baseHit == BaseHit.HOMERUN) {
                homerun();
            } else {
                EventType eventType = baseHit.toEventType();
                int numberOfErrors = errorCountDistribution.getNumberOfErrors(eventType, baseSituation, dieFactory);
                if (numberOfErrors == 0) {
                    Advances advances = baseHitAdvanceDistribution.pickOne(
                            baseHit, baseSituation, outs, dieFactory);
                    PlayOutcome p = new PlayOutcome(eventType, advances, numberOfErrors);
                    registerBaseHitStats(baseHit, p.getNumberOfRuns());
                    addOutcome(p, p);
                } else {
                    Advances advances = errorAdvanceDistribution.pickOne(
                            ErrorAdvanceKey.of(eventType, numberOfErrors), baseSituation, outs, dieFactory);
                    PlayOutcome actual = new PlayOutcome(eventType, advances, numberOfErrors);
                    
                    // TODO: pickMostCommon, or new method that picks one from the distribution, with the
                    // additional condition that the selected Advances cannot have any errors? For example,
                    // add method AdvanceDistribution::pickOne(..., Predicate<? super Advances> predicate)
                    Advances idealAdvances = baseHitAdvanceDistribution.pickMostCommon(baseHit, baseSituation, outs);
                    PlayOutcome ideal = new PlayOutcome(eventType, idealAdvances, 0);
                    
                    addOutcome(actual, ideal);
                    // TODO: Not sure this is the best way to decide which runs should be awarded as RBIs
                    // for the batter.
                    int rbis = ideal.getNumberOfRuns();
                    registerBaseHitStats(baseHit, rbis);
                }
            }
        }
        
        private void registerBaseHitStats(BaseHit baseHit, int rbis) {
            baseHit.getBattingStat().ifPresent(builder::addBattingStat);
            baseHit.getPitchingStat().ifPresent(builder::addPitchingStat);
            builder.addStat(BattingStat.HITS, PitchingStat.HITS);
            builder.addBattingStat(PrimitiveBattingStat.RUNS_BATTED_IN, rbis);
        }
        
        private void homerun() {
            Advances advances = Advances.homerun(baseSituation.getOccupiedBases());
            PlayOutcome hr = new PlayOutcome(EventType.HOMERUN, advances);
            addOutcome(hr, hr);
            registerBaseHitStats(BaseHit.HOMERUN, hr.getNumberOfRuns());
        }

        private void strikeout() {
            PlayOutcome so = PlayOutcome.strikeout();
            addOutcome(so, so);
            builder.addStat(BattingStat.STRIKEOUTS, PitchingStat.STRIKEOUTS);
        }

        private void batterAwardedFirst(Outcome outcome) {
            assert outcome == Outcome.WALK || outcome == Outcome.HIT_BY_PITCH;
            Advances advances = Advances.batterAwardedFirstBase(baseSituation.getOccupiedBases());
            EventType type = (outcome == Outcome.WALK)
                    ? EventType.WALK
                    : EventType.HIT_BY_PITCH;
            PlayOutcome p = new PlayOutcome(type, advances);
            if (outcome == Outcome.WALK) {
                builder.addStat(PrimitiveBattingStat.WALKS, PrimitivePitchingStat.WALKS);
            } else {
                builder.addStat(PrimitiveBattingStat.HIT_BY_PITCHES, PrimitivePitchingStat.HIT_BY_PITCHES);
            }
            builder.addBattingStat(PrimitiveBattingStat.RUNS_BATTED_IN, advances.getNumberOfRuns());
            addOutcome(p, p);
        }

        //private int fieldersChoices;
        // privat int sacrificeFlies;
        private void out() {
            OutLocation location = getOutLocation();
            int numberOfErrors = errorCountDistribution.getNumberOfErrors(EventType.OUT, baseSituation, dieFactory);
            if (numberOfErrors == 0) {
                outWithoutError(location);
            } else {
                errorOnOut(location, numberOfErrors);
            }
        }

        private void outWithoutError(OutLocation location) {
            boolean convertToFieldersChoice = (location == OutLocation.INFIELD) && 
                    fieldersChoiceProbabilities.test(baseSituation, dieFactory);
            EventType resultingType = convertToFieldersChoice
                    ? EventType.FIELDERS_CHOICE
                    : EventType.OUT;

            OutAdvanceKey key = OutAdvanceKey.of(resultingType, location, outs);
            Advances advances = outAdvanceDistribution.pickOne(key, baseSituation, outs, dieFactory);
//                if (convertToFieldersChoice) {
//                    ++fieldersChoices;
//                    System.out.println("Fielder's Choice " + fieldersChoices);
//                }
//                if (resultingType == EventType.OUT && location == OutLocation.OUTFIELD && advances.contains(Advance.safe(Base.THIRD, Base.HOME)) {
//                    ++sacrificeFlies;
//                    //System.out.println("Sacrifice Fly " + sacrificeFlies);
//                }
            if (resultingType == EventType.OUT && outs < 2 && location == OutLocation.OUTFIELD
                    && advances.didRunnerAdvanceSafely(Base.THIRD)) {
                builder.addBattingStat(PrimitiveBattingStat.SACRIFICE_FLIES);
            }
            builder.addBattingStat(PrimitiveBattingStat.RUNS_BATTED_IN, advances.getNumberOfRuns());
            PlayOutcome p = new PlayOutcome(resultingType, advances);
            addOutcome(p, p);
        }

        private void errorOnOut(OutLocation location, int numberOfErrors) {
            ErrorAdvanceKey key = ErrorAdvanceKey.of(EventType.OUT, numberOfErrors);
            Advances advances = errorAdvanceDistribution.pickOne(key, baseSituation, outs, dieFactory);
            // TODO: This will give the incorrect type in the case where the batter is thrown
            // out at some other base than first.
            EventType actualType = advances.didBatterReachBase()
                    ? EventType.REACHED_ON_ERROR
                    : EventType.OUT;
            PlayOutcome actual = new PlayOutcome(actualType, advances, numberOfErrors);
            
            Advances idealAdvances = outAdvanceDistribution.pickMostCommon(
                    OutAdvanceKey.of(EventType.OUT, location, outs), baseSituation, outs);
            PlayOutcome ideal = new PlayOutcome(EventType.OUT, idealAdvances, 0);

            addOutcome(actual, ideal);
        }
        
        private OutLocation getOutLocation() {
            // TODO: Get from play-by-play data. For now we use a 65-35 split.
            return Math.random() < 0.65
                    ? OutLocation.INFIELD
                    : OutLocation.OUTFIELD;
            
        }

        private void addOutcome(PlayOutcome actual, PlayOutcome ideal) {
            // If the batter should have been out by now, e.g. because his time at bat
            // was prolonged by an ERROR_ON_FOUL_FLY, any subsequent ideal play is the NO PLAY,
            // since the play should not have happened at all in an ideal inning.
            builder.addOutcome(actual, batterShouldHaveBeenOut ? PlayOutcome.noPlay() : ideal);
            ResultOfAdvance roa = baseSituation.advanceRunners(new BaseRunner(batter, pitcher), 
                    actual.getAdvances());
            ImmutableList<BaseRunner> runs = roa.getRunnersThatScored();
            builder.runsScored(runs);
            baseSituation = roa.getNewSituation();
            outs += actual.getNumberOfOuts();
            runsNeededToWin = runsNeededToWin.updateWithRunsScored(runs.size());
        }
    }
}
