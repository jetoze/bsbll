package bsbll.matchup;

import bsbll.card.PlayerCard;
import bsbll.matchup.Log5BasedMatchupRunner.Outcome;

/**
 * The matchup between a batter and a pitcher.
 */
public interface MatchupRunner {
    
    // TODO: Should this method take two Players as input, and delegate to the implementation
    // to lookup corresponding PlayerCards?
    Outcome run(PlayerCard batter, PlayerCard pitcher);
}