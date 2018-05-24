package bsbll.matchup;

import bsbll.matchup.Log5BasedMatchupRunner.Outcome;
import bsbll.player.Player;

/**
 * The matchup between a batter and a pitcher.
 */
public interface MatchupRunner {
    Outcome run(Player batter, Player pitcher);
}