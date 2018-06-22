package bsbll.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseHit;
import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.RunsScored.Run;
import bsbll.game.params.GamePlayParams;
import bsbll.game.params.OutAdvanceKey;
import bsbll.game.params.OutLocation;
import bsbll.game.play.EventType;
import bsbll.game.play.Play;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;

/**
 * The official scorer's reconstruction of an inning that contains potentially unearned runs.
 */
final class ReconstructedInning {
    private final Inning inning;
    private final ImmutableList<Play> actualPlays;
    // XXX: GamePlayParams offers more functionality than what is needed here. Refactor to 
    // pass in a trimmed down service.
    private final GamePlayParams gamePlayParams;
    private BaseSituation baseSituation = BaseSituation.empty();
    private int outs;
    private boolean batterShouldBeOut;
    
    public ReconstructedInning(HalfInning.Summary summary, GamePlayParams gamePlayParams) {
        this.inning = summary.getInning();
        this.actualPlays = summary.getPlays();
        this.gamePlayParams = requireNonNull(gamePlayParams);
    }

    public static ReconstructedInning of(HalfInning.Summary summary, GamePlayParams gamePlayParams) {
        return new ReconstructedInning(summary, gamePlayParams);
    }
    
    public ImmutableList<Run> getEarnedRuns() {
        // TODO: This situation needs to change once we implement pitcher substitutions, when the
        // pitcher responsible for a batter can change due to a fielder's choice.
        // TODO: Also, when we have pitcher substitutions we must distinguish between team unearned 
        // runs and pitcher unearned runs. Add class EarnedRun, with a flag that tells us if the run
        // is earned for both the team and the pitcher, or just the pitcher.
        ImmutableList.Builder<Run> earnedRuns = ImmutableList.builder();
        Player previousBatter = null;
        
        for (Play actualPlay : actualPlays) {
            if (actualPlay.getBatter() != previousBatter) {
                previousBatter = actualPlay.getBatter();
                batterShouldBeOut = false;
            } else if (batterShouldBeOut) {
                continue;
            }
            Play idealPlay = getIdealPlay(actualPlay);
            handleIdealPlay(idealPlay, earnedRuns);
            previousBatter = idealPlay.getBatter();
        }
        return earnedRuns.build();
    }

    private void handleIdealPlay(Play idealPlay, ImmutableList.Builder<Run> earnedRuns) {
        if (idealPlay.isNoPlay()) {
            return;
        }
        // FIXME: This logic is broken. Take the following example:
        //   1. Single
        //   2. Passed Ball - runner advances to second
        //   3. Single - runner scores
        // The ideal version of the above, as reconstructed here, is:
        //   1. Single
        //   2. No Play - runner stays on first
        //   3. Single. The original advances are [B-1][2-H]. After applying the
        //      filter here, we end up with [B-1], and we get two runners on first base.
        // TODO: Verify this in a unit test.
        Advances applicableAdvances = idealPlay.getAdvances().keep(b -> b == Base.HOME || baseSituation.isOccupied(b));
        ResultOfAdvance roa = baseSituation.advanceRunners(
                new BaseRunner(idealPlay.getBatter(), idealPlay.getPitcher()), 
                applicableAdvances);
        if (outs < 3) {
            // TODO: Once we implement pitcher substitutions this will have to be treated 
            // differently. The run will be unearned for the team, but could still be earned
            // for the pitcher.
            roa.getRunnersThatScored().stream()
                .map(br -> new Run(inning, br))
                .forEach(earnedRuns::add);
        }
        baseSituation = roa.getNewSituation();
        outs += idealPlay.getNumberOfOuts();
    }
    
    private Play getIdealPlay(Play actualPlay) {
        if (!actualPlay.isErrorOrPassedBall()) {
            return actualPlay;
        }
        EventType type = actualPlay.getOutcome().getType();
        if (type == EventType.PASSED_BALL) {
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        assert actualPlay.getNumberOfErrors() > 0;
        if (type == EventType.ERROR_ON_FOUL_FLY) {
            batterShouldBeOut = true;
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        if (type == EventType.REACHED_ON_ERROR || type == EventType.OUT) {
            return idealPlayOnOut(actualPlay, type);
        } else if (type.isHit()) {
            return idealPlayOnBaseHit(actualPlay, type);
        } else {
            throw new RuntimeException("TODO: Implement me");
        }
    }

    private Play idealPlayOnOut(Play actualPlay, EventType type) {
        batterShouldBeOut = (type == EventType.REACHED_ON_ERROR); // If OUT, the batter *is* out.
        // TODO: The OutLocation should be the same that was used when the original OUT
        // was turned into a REACHED_ON_ERROR by the GamePlayParams. How do we accomplish
        // that? Store the OutLocation as an optional parameter in PlayOutcome?
        OutLocation location = gamePlayParams.getOutLocation();
        // XXX: outs keeps track of how many outs *should* have been recorded. This can
        // be >= 3. The advance distributions rightfully expect the number of outs to be
        // < 3, so if we end up in that situation we use 2.
        int outsToUse = Math.min(2, outs);
        OutAdvanceKey key = OutAdvanceKey.of(EventType.OUT, location, outsToUse);
        // TODO: What if the most common advance is a double play? Can the official scorer assume
        // a double-play as the ideal play when reconstructing the inning? I don't think so. On the
        // other hand, I don't think the most common advance will ever be a double-play. On the third
        // hand, we should arguably not be using the most common advances, but rather just pick one.
        // That will require us to add an overloaded advances picker that takes an additional
        // Predicate as input. The predicate would also filter out advances with errors.
        Advances advances = gamePlayParams.getMostCommonAdvancesOnOut(key, baseSituation, outsToUse);
        return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), new PlayOutcome(EventType.OUT, advances));
    }

    private Play idealPlayOnBaseHit(Play actualPlay, EventType type) {
        BaseHit hit = BaseHit.fromEventType(type);
        // TODO: We should probably not be using the most common advances here, but rather pick one.
        // (cf. the case of REACHED_ON_ERROR)
        Advances advances = gamePlayParams.getMostCommonAdvancesOnBaseHit(hit, baseSituation, Math.min(2, outs));
        return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), new PlayOutcome(type, advances));
    }
}
