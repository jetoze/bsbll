package bsbll.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.bases.Advances;
import bsbll.bases.Base;
import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.RunsScored.Run;
import bsbll.game.params.GamePlayParams;
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
        
        for (Play play : actualPlays) {
            if (play.getBatter() != previousBatter) {
                previousBatter = play.getBatter();
                batterShouldBeOut = true;
            } else if (batterShouldBeOut) {
                continue;
            }
            if (play.isErrorOrPassedBall()) {
                play = getIdealPlay(play);
            }
            Advances applicableAdvances = play.getAdvances().keep(b -> b == Base.HOME || baseSituation.isOccupied(b));
            ResultOfAdvance roa = baseSituation.advanceRunners(new BaseRunner(play.getBatter(), play.getPitcher()), applicableAdvances);
            if (outs < 3) {
                roa.getRunnersThatScored().stream()
                    .map(br -> new Run(inning, br))
                    .forEach(earnedRuns::add);
            }
            baseSituation = roa.getNewSituation();
            outs += play.getNumberOfOuts();
        }
        return earnedRuns.build();
    }
    
    private Play getIdealPlay(Play actualPlay) {
        EventType type = actualPlay.getOutcome().getType();
        if (type == EventType.PASSED_BALL || type == EventType.ERROR_ON_FOUL_FLY) {
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        assert actualPlay.getNumberOfErrors() > 0;
        if (type == EventType.ERROR_ON_FOUL_FLY) {
            batterShouldBeOut = true;
            return new Play(actualPlay.getBatter(), actualPlay.getPitcher(), PlayOutcome.noPlay());
        }
        if (type == EventType.REACHED_ON_ERROR) {
            throw new RuntimeException("TODO: Implement me");
        } else if (type.isHit()) {
            throw new RuntimeException("TODO: Implement me");
        } else {
            throw new RuntimeException("TODO: Implement me");
        }
    }
}
