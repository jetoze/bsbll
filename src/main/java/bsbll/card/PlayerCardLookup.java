package bsbll.card;

import bsbll.league.LeagueId;
import bsbll.player.Player;

/**
 * Defines a lookup of PlayerCards.
 * <p>
 * Implementation will typically, but not necessarily, be tied to a specific season.
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
     * Returns the (batting) player card for a league as a whole.
     */
    PlayerCard getLeagueCard(LeagueId leagueId);
    
}
