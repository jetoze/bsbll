package bsbll.stats;

import static java.util.Objects.requireNonNull;

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
    
    
    /**
     * A permanently empty lookup, that hands out results from empty stat lines.
     */
    public static PlayerStatLookup EMPTY = new PlayerStatLookup() {
        
        @Override
        public <T> T getPitchingStat(Player player, PitchingStat<T> stat) {
            requireNonNull(player);
            requireNonNull(stat);
            return PitchingStatLine.empty().get(stat);
        }
        
        @Override
        public <T> T getBattingStat(Player player, BattingStat<T> stat) {
            requireNonNull(player);
            requireNonNull(stat);
            return BattingStatLine.empty().get(stat);
        }
    };
}
