package bsbll.card;

import bsbll.player.Player;

/**
 * Defines a lookup of PlayerCards.
 * <p>
 * Implementation will typically, but not necessarily, be tied to a specific league and season.
 */
public interface PlayerCardLookup {
    /**
     * Returns the batting card for a player.
     */
    PlayerCard getBattingCard(Player player);

    /**
     * Returns the pitching card for a player;
     */
    PlayerCard getPitchingCard(Player player);
    
    /**
     * Returns the (batting) player card for the league as a whole.
     */
    PlayerCard getLeagueCard();
    
}
