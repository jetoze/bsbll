package bsbll.game;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.game.RunsScored.Run;
import bsbll.player.Player;
import bsbll.stats.PitchingStat;
import bsbll.stats.PlayerStatLookup;
import bsbll.stats.WinLossRecord;
import bsbll.team.Lineup;
import bsbll.team.TeamId;
import tzeth.collections.ImCollectors;

public final class OfficialScorer { // TODO: Is this a good abstraction?
    private final PlayerStatLookup statLookup;
    
    public OfficialScorer() {
        this(PlayerStatLookup.EMPTY);
    }
    
    public OfficialScorer(PlayerStatLookup statLookup) {
        this.statLookup = requireNonNull(statLookup);
    }
    
    /**
     * Gets the pitcher who should get credit for the win. Should not
     * be called in a tie game.
     */
    public PitcherOfRecord getWinningPitcher(TeamId homeTeamId,
                                             Lineup homeTeamLineup,
                                             TeamId visitingTeamId,
                                             Lineup visitingTeamLineup, 
                                             RunsScored runs) {
        // TODO: Enforce the rule that a starting pitcher must pitch at least 5 innings
        // in order to get credit for a win. Since pitching substitutions haven't been
        // implemented yet, the winning pitcher is simply the pitcher on the winning team.
        GameResult result = runs.toGameResult(homeTeamId, visitingTeamId);
        checkArgument(!result.isTie(), "A tie game does not have a winning pitcher");
        Player pitcher = (result.getHomeScore() > result.getVisitingScore())
                ? homeTeamLineup.getPitcher()
                : visitingTeamLineup.getPitcher();
        int wins = statLookup.getPitchingStat(pitcher, PitchingStat.WINS) + 1;
        int losses = statLookup.getPitchingStat(pitcher, PitchingStat.LOSSES);
        return new PitcherOfRecord(pitcher, Decision.WIN, new WinLossRecord(wins, losses));
    }
    
    /**
     * Gets the pitcher who should get credit for the loss. Should not
     * be called in a tie game.
     */
    public PitcherOfRecord getLosingPitcher(RunsScored runs) {
        Player pitcher = runs.getLosingPitcher();
        int wins = statLookup.getPitchingStat(pitcher, PitchingStat.WINS);
        int losses = statLookup.getPitchingStat(pitcher, PitchingStat.LOSSES) + 1;
        return new PitcherOfRecord(pitcher, Decision.LOSS, new WinLossRecord(wins, losses));
    }

    public ImmutableList<BaseRunner> getEarnedRuns(HalfInning.Summary inningSummary) {
        ImmutableList<Run> allRuns = inningSummary.getRuns();
        if (allRuns.isEmpty()) {
            return ImmutableList.of();
        }
        if (inningSummary.isEarnedRunReconstructionNeeded()) {
            return ReconstructedInning.of(inningSummary).getEarnedRuns();
        } else {
            return allRuns.stream()
                    .map(r -> new BaseRunner(r.getRunner(), r.getResponsiblePitcher()))
                    .collect(ImCollectors.toList());
        }
    }
    
    // TODO: Functionality for earned runs, rbis.
    
}
