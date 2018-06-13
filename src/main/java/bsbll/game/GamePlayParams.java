package bsbll.game;

import static java.util.Objects.requireNonNull;

import javax.annotation.concurrent.Immutable;

import bsbll.game.params.BaseHitAdvanceDistribution;
import bsbll.matchup.MatchupRunner;
import bsbll.player.Player;

/**
 * Various parameters that control the game play, such as the probability that a batted out
 * results in an out, or that a runner on first takes third base on a base hit.
 */
@Immutable
public final class GamePlayParams { // TODO: Do I need a better name?
    private final MatchupRunner matchupRunner;
    private final BaseHitAdvanceDistribution baseHitAdvanceDistribution;
    
    public GamePlayParams(MatchupRunner matchupRunner,
                          BaseHitAdvanceDistribution baseHitAdvanceDistribution) {
        this.matchupRunner = requireNonNull(matchupRunner);
        this.baseHitAdvanceDistribution = requireNonNull(baseHitAdvanceDistribution);
    }

    /**
     * 
     * @return the MatchupRunner that will be asked to simulate the matchup
     *         between the pitcher and the batters in the game
     */
    public MatchupRunner getMatchupRunner() {
        return matchupRunner;
    }
    
    public MatchupRunner.Outcome runMatchup(Player batter, Player pitcher) {
        return matchupRunner.run(batter, pitcher);
    }

    public BaseHitAdvanceDistribution getBaseHitAdvanceDistribution() {
        return baseHitAdvanceDistribution;
    }
}
