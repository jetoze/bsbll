package bsbll.card.season;

import static java.util.Objects.requireNonNull;

import bsbll.Year;
import bsbll.card.PlayerCardLookup;
import bsbll.game.GameContext;
import bsbll.league.LeagueId;
import bsbll.team.Team;

public final class Season {
    private final LeagueId leagueId;
    private final Year year;
    private final PlayerCardLookup playerCardLookup;
    
    public Season(LeagueId leagueId, Year year, PlayerCardLookup playerCardLookup) {
        this.leagueId = requireNonNull(leagueId);
        this.year = requireNonNull(year);
        this.playerCardLookup = requireNonNull(playerCardLookup);
    }

    public GameContext newGameContext(Team homeTeam, Team visitingTeam) {
        return new GameContext(homeTeam, visitingTeam, this.leagueId, this.playerCardLookup);
    }
    
    @Override
    public String toString() {
        return year + " " + leagueId;
    }
}
