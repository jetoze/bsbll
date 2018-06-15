package bsbll.game.event;

import java.util.Optional;

import bsbll.bases.BaseSituation;
import bsbll.game.Inning;
import bsbll.game.play.PlayOutcome;
import bsbll.player.Player;

/**
 * Looks for {@code GameEvent}s to report in the box score for a game.
 */
public interface GameEventDetector {
    Optional<GameEvent> examine(PlayOutcome outcome, Inning inning, Player batter, Player pitcher, 
                int outs, BaseSituation baseSituation);
    
    /**
     * GameEventDetector that does not report any events.
     */
    public static final GameEventDetector NO_EVENTS = (outcome, inning, batter, pitcher, outs, baseSituation) -> Optional.empty();
    
}
