package bsbll.game;

import static java.util.Objects.requireNonNull;

import bsbll.card.PlayerCardLookup;
import bsbll.league.LeagueId;
import bsbll.team.Team;

public final class GameContext {
    private final Team homeTeam;
    private final Team visitingTeam;
    private final LeagueId leagueId;
    private final PlayerCardLookup playerCardLookup;

    public GameContext(Team homeTeam, Team visitingTeam, LeagueId leagueId,
            PlayerCardLookup playerCardLookup) {
        this.homeTeam = requireNonNull(homeTeam);
        this.visitingTeam = requireNonNull(visitingTeam);
        this.leagueId = requireNonNull(leagueId);
        this.playerCardLookup = requireNonNull(playerCardLookup);
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getVisitingTeam() {
        return visitingTeam;
    }

    public LeagueId getLeagueId() {
        return leagueId;
    }

    public PlayerCardLookup getPlayerCardLookup() {
        return playerCardLookup;
    }
}
