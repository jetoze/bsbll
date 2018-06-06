package bsbll.game;

import static java.util.Objects.requireNonNull;

import bsbll.team.Lineup;

public final class BoxScore {
    private final LineScore lineScore;
    private final Lineup homeLineup;
    private final Lineup visitingLineup;
    private final PlayerGameStats playerStats;
    private final GameEvents gameEvents;

    public BoxScore(LineScore lineScore, Lineup homeLineup, Lineup visitingLineup,
            PlayerGameStats playerStats, GameEvents gameEvents) {
        this.lineScore = requireNonNull(lineScore);
        this.homeLineup = requireNonNull(homeLineup);
        this.visitingLineup = requireNonNull(visitingLineup);
        this.playerStats = requireNonNull(playerStats);
        this.gameEvents = requireNonNull(gameEvents);
    }

    public LineScore getLineScore() {
        return lineScore;
    }

    public Lineup getHomeLineup() {
        return homeLineup;
    }

    public Lineup getVisitingLineup() {
        return visitingLineup;
    }

    public PlayerGameStats getPlayerStats() {
        return playerStats;
    }
    
    public GameEvents getGameEvents() {
        return gameEvents;
    }

    public GameResult toGameResult() {
        return lineScore.toGameResult();
    }
}
