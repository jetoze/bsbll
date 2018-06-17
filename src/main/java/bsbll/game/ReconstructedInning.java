package bsbll.game;

import com.google.common.collect.ImmutableList;

import bsbll.bases.BaseSituation;
import bsbll.bases.BaseSituation.ResultOfAdvance;
import bsbll.game.play.Play;

/**
 * The official scorer's reconstruction of an inning that contains potentially unearned runs.
 */
final class ReconstructedInning {
    private final ImmutableList<Play> idealPlays;
    
    public ReconstructedInning(HalfInning.Summary summary) {
        this.idealPlays = summary.getIdealPlays();
    }

    public static ReconstructedInning of(HalfInning.Summary summary) {
        return new ReconstructedInning(summary);
    }
    
    public ImmutableList<BaseRunner> getEarnedRuns() {
        // TODO: This situation needs to change once we implement pitcher substitutions, when the
        // pitcher responsible for a batter can change due to a fielder's choice.
        // TODO: Also, when we have pitcher substitutions we must distinguish between team unearned 
        // runs and pitcher unearned runs. Add class EarnedRun, with a flag that tells us if the run
        // is earned for both the team and the pitcher, or just the pitcher.
        ImmutableList.Builder<BaseRunner> earnedRuns = ImmutableList.builder();
        BaseSituation bases = BaseSituation.empty();
        int outs = 0;
        for (Play play : idealPlays) {
            ResultOfAdvance roa = play.advanceRunners(bases);
            if (outs < 3) {
                earnedRuns.addAll(roa.getRunnersThatScored());
            }
            bases = roa.getNewSituation();
            outs += play.getNumberOfOuts();
        }
        return earnedRuns.build();
    }
}
