package bsbll.game;

import static java.util.Objects.requireNonNull;

import bsbll.team.Lineup;

public final class BoxScore {
    private final LineScore lineScore;
    private final Lineup homeLineup;
    private final Lineup visitingLineup;
    private final PlayerGameStats playerStats;

    public BoxScore(LineScore lineScore, Lineup homeLineup, Lineup visitingLineup,
            PlayerGameStats playerStats) {
        this.lineScore = requireNonNull(lineScore);
        this.homeLineup = requireNonNull(homeLineup);
        this.visitingLineup = requireNonNull(visitingLineup);
        this.playerStats = requireNonNull(playerStats);
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
    
    public GameResult toGameResult() {
        return lineScore.toGameResult();
    }
}
