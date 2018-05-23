package bsbll.game;

import bsbll.matchup.Matchup;
import bsbll.player.Player;

public interface MatchupFactory {
    Matchup createMatchup(Player batter, Player pitcher);
}
