package bsbll.stats;

import bsbll.player.Player;

/**
 * Defines a lookup of player stats, both batting and pitching.
 */
public interface PlayerStatLookup {
    /**
     * Looks up the given player's value for the given batting stat.
     */
    <T> T getBattingStat(Player player, BattingStat<T> stat);
    
    /**
     * Looks up the given player's value for the given pitching stat.
     */
    <T> T getPitchingStat(Player player, PitchingStat<T> stat);
}
