package bsbll.game;

import java.util.Optional;

import bsbll.matchup.MatchupRunner.Outcome;
import bsbll.player.Player;

/**
 * Looks for {@code GameEvent}s to report in the box score for a game.
 */
public interface GameEventDetector {
    Optional<GameEvent> examine(Outcome outcome, Player batter, Player pitcher, int inning, 
                int outs, BaseSituation baseSituation);
    
    /**
     * GameEventDetector that does not report any events.
     */
    public static final GameEventDetector NO_EVENTS = (outcome, batter, pitcher, inning, outs, baseSituation) -> Optional.empty();
    
}
