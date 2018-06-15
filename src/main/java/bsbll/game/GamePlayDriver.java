package bsbll.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advances;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.game.params.BaseHitAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceDistribution;
import bsbll.game.params.ErrorAdvanceKey;
import bsbll.game.params.ErrorCountDistribution;
import bsbll.game.params.FieldersChoiceProbabilities;
import bsbll.game.params.OutAdvanceDistribution;
import bsbll.game.params.OutAdvanceKey;
import bsbll.game.params.OutLocation;
import bsbll.game.play.EventType;
import bsbll.game.play.PlayOutcome;
import bsbll.matchup.MatchupRunner;
import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

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
    
    public GamePlayDriver(MatchupRunner matchupRunner,
                          BaseHitAdvanceDistribution baseHitAdvanceDistribution,
                          OutAdvanceDistribution outAdvanceDistribution,
                          FieldersChoiceProbabilities fieldersChoiceProbabilities,
                          ErrorCountDistribution errorCountDistribution,
                          ErrorAdvanceDistribution errorAdvanceDistribution) {
        this.matchupRunner = requireNonNull(matchupRunner);
        this.baseHitAdvanceDistribution = requireNonNull(baseHitAdvanceDistribution);
        this.outAdvanceDistribution = requireNonNull(outAdvanceDistribution);
        this.fieldersChoiceProbabilities = requireNonNull(fieldersChoiceProbabilities);
        this.errorCountDistribution = requireNonNull(errorCountDistribution);
        this.errorAdvanceDistribution = requireNonNull(errorAdvanceDistribution);
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
    
    // TODO: We pass in the same four parameters in both calls right now. Add a param class?
    
    // TODO: Depending on how we go about implementing earned run detection, we may also
    // want to return a list of "ideal" plays, i.e. how the matchup would have played out
    // in the absence of errors and passed balls. What's a good way of doing that? One way
    // that comes to mind is to add an "idealOutcome" property to PlayOutcome, i.e. ask the
    // PlayOutcome itself what the ideal version of it is. For normal outcomes, this will
    // simply be the PlayOutcome itself.
    
    /**
     * Returns the plays, if any, that take place before the batter-pitcher
     * matchup completes. This includes things like stolen base attempts, balks,
     * or wild pitches.
     * <p>
     * Note that if any of the plays results in outs, we may reach the end of
     * the inning (three outs), in which case the matchup should be terminated,
     * and {@link #runMatchup(Player, Player, BaseSituation, int) runMatchup} should
     * not be called. Ditto if any of these plays result in a walk-off run.
     */
    public ImmutableList<PlayOutcome> preMatchupCompletionPlays(Player batter, 
                                                                Player pitcher, 
                                                                BaseSituation baseSituation, 
                                                                int outs) {
        // TODO: Implement me.
        return ImmutableList.of();
    }
    
    /**
     * Generates the basic outcome of the batter-pitcher matchup itself.
     */
    public PlayOutcome runMatchup(Player batter, Player pitcher, BaseSituation baseSituation, int outs) {
        // TODO: Could there be a case where we need to return more than one play here?
        // I can't think of any at the moment.
        requireNonNull(batter);
        requireNonNull(pitcher);
        Outcome basicOutcome = matchupRunner.run(batter, pitcher);
        return resultingPlay(baseSituation, basicOutcome, outs);
    }

    /**
     * Returns the play that results from the outcome of the matchup.
     */
    private PlayOutcome resultingPlay(BaseSituation baseSituation, Outcome basicOutcome, int outs) {
        switch (basicOutcome) {
        case SINGLE:
            return baseHit(BaseHit.SINGLE, baseSituation, outs);
        case DOUBLE:
            return baseHit(BaseHit.DOUBLE, baseSituation, outs);
        case TRIPLE:
            return baseHit(BaseHit.TRIPLE, baseSituation, outs);
        case HOMERUN:
            return baseHit(BaseHit.HOMERUN, baseSituation, outs);
        case STRIKEOUT:
            // TODO: Dropped third strike could result in the batter going to first,
            // without an out being recorded.
            return PlayOutcome.builder(EventType.STRIKEOUT).build();
        case WALK:
            return batterAwardedFirst(baseSituation, basicOutcome);
        case HIT_BY_PITCH:
            return batterAwardedFirst(baseSituation, basicOutcome);
        case OUT:
            return out(baseSituation, outs);
        default:
            throw new AssertionError("Unexpected outcome: " + basicOutcome);
        }
    }
    
    private PlayOutcome baseHit(BaseHit baseHit, BaseSituation baseSituation, int outs) {
        if (baseHit == BaseHit.HOMERUN) {
            return homerun(baseSituation);
        } else {
            EventType eventType = baseHit.toEventType();
            int numberOfErrors = errorCountDistribution.getNumberOfErrors(eventType, baseSituation);
            Advances advances = (numberOfErrors == 0)
                    ? baseHitAdvanceDistribution.pickOne(baseHit, baseSituation, outs)
                    : errorAdvanceDistribution.pickOne(ErrorAdvanceKey.of(eventType, numberOfErrors), baseSituation, outs);
            return new PlayOutcome(eventType, advances, numberOfErrors);
        }
    }
    
    private PlayOutcome homerun(BaseSituation baseSituation) {
        Advances advances = Advances.homerun(baseSituation.getOccupiedBases());
        return new PlayOutcome(EventType.HOMERUN, advances);
    }
    
    private PlayOutcome batterAwardedFirst(BaseSituation baseSituation, Outcome outcome) {
        assert outcome == Outcome.WALK || outcome == Outcome.HIT_BY_PITCH;
        Advances advances = Advances.batterAwardedFirstBase(baseSituation.getOccupiedBases());
        EventType type = (outcome == Outcome.WALK)
                ? EventType.WALK
                : EventType.HIT_BY_PITCH;
        return new PlayOutcome(type, advances);
    }

    //private int fieldersChoices;
    // privat int sacrificeFlies;
    private PlayOutcome out(BaseSituation baseSituation, int outs) {
        int numberOfErrors = errorCountDistribution.getNumberOfErrors(EventType.OUT, baseSituation);
        if (numberOfErrors == 0) {
            OutLocation location = getOutLocation();
            boolean convertToFieldersChoice = (location == OutLocation.INFIELD) && 
                    fieldersChoiceProbabilities.test(baseSituation);
            EventType resultingType = convertToFieldersChoice
                    ? EventType.FIELDERS_CHOICE
                    : EventType.OUT;

            OutAdvanceKey key = OutAdvanceKey.of(resultingType, location, outs);
            Advances advances = outAdvanceDistribution.pickOne(key, baseSituation, outs);
//            if (convertToFieldersChoice) {
//                ++fieldersChoices;
//                System.out.println("Fielder's Choice " + fieldersChoices);
//            }
//            if (resultingType == EventType.OUT && location == OutLocation.OUTFIELD && advances.contains(Advance.safe(Base.THIRD, Base.HOME)) {
//                ++sacrificeFlies;
//                //System.out.println("Sacrifice Fly " + sacrificeFlies);
//            }
            return new PlayOutcome(resultingType, advances);
        } else {
            ErrorAdvanceKey key = ErrorAdvanceKey.of(EventType.OUT, numberOfErrors);
            Advances advances = errorAdvanceDistribution.pickOne(key, baseSituation, outs);
            // TODO: This will give the incorrect type in the case where the batter is thrown
            // out at some other base than first.
            EventType actualType = advances.didBatterReachBase()
                    ? EventType.REACHED_ON_ERROR
                    : EventType.OUT;
            return new PlayOutcome(actualType, advances, numberOfErrors);
        }
    }
    
    private OutLocation getOutLocation() {
        // TODO: Get from play-by-play data. For now we use a 65-35 split.
        return Math.random() < 0.65
                ? OutLocation.INFIELD
                : OutLocation.OUTFIELD;
        
    }
}
