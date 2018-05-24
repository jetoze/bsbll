package bsbll.matchup;

import bsbll.card.PlayerCard;
import bsbll.matchup.Log5BasedMatchupRunner.Outcome;

/**
 * The matchup between a batter and a pitcher.
 */
public interface MatchupRunner {
    Outcome run(PlayerCard batter, PlayerCard pitcher);
}