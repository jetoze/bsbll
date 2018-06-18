package bsbll.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.RunsScored.Run;
import bsbll.game.params.GamePlayParams;
import bsbll.game.play.Play;

/**
 * The official scorer's reconstruction of an inning that contains potentially unearned runs.
 */
final class ReconstructedInning {
    private final Inning inning;
    private final ImmutableList<Play> actualPlays;
    // XXX: GamePlayParams offers more functionality than what is needed here. Refactor to 
    // pass in a trimmed down service.
    private final GamePlayParams gamePlayParams;
    
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
        BaseSituation bases = BaseSituation.empty();
        int outs = 0;
        for (Play play : actualPlays) {
            if (play.isErrorOrPassedBall()) { // TODO: Or Wild pitch
                // TODO: Create an ideal version of the play.
            }
            ResultOfAdvance roa = play.advanceRunners(bases);
            if (outs < 3) {
                roa.getRunnersThatScored().stream()
                    .map(br -> new Run(inning, br))
                    .forEach(earnedRuns::add);
            }
            bases = roa.getNewSituation();
            outs += play.getNumberOfOuts();
        }
        return earnedRuns.build();
    }
}
