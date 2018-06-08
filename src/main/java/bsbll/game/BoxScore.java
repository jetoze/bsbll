package bsbll.game;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import bsbll.player.Player;
import bsbll.team.Lineup;
import bsbll.team.Team;

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

    public Team getHomeTeam() {
        return lineScore.getHomeTeam();
    }
    
    public Team getVisitingTeam() {
        return lineScore.getVisitingTeam();
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
    
    public <T extends GameEvent> ImmutableList<T> getGameEvents(Class<T> type) {
        return this.gameEvents.getEvents(type);
    }

    public GameResult toGameResult() {
        return lineScore.toGameResult();
    }
    
    /**
     * Returns the team the given player played for in this game.
     */
    public Team getTeam(Player player) {
        requireNonNull(player);
        if (homeLineup.contains(player)) {
            return lineScore.getHomeTeam();
        } else if (visitingLineup.contains(player)) {
            return lineScore.getVisitingTeam();
        } else {
            throw new IllegalArgumentException("Player did not play in this game: " + player);
        }
    }
}
