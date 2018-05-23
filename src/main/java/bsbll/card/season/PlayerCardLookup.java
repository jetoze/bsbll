package bsbll.card.season;

import bsbll.card.PlayerCard;
import bsbll.league.LeagueId;
import bsbll.player.Player;

/**
 * Defines a lookup of PlayerCards for a given season.
 */
public interface PlayerCardLookup {
    // TODO: Does this interface need a better name, to reflect the fact that it is tied
    // to a specific season? It's in the season package, but is that enough?
    
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
