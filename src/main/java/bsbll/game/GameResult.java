package bsbll.game;

import static java.util.Objects.requireNonNull;
import static tzeth.preconds.MorePreconditions.checkNotNegative;

import javax.annotation.concurrent.Immutable;

import bsbll.team.TeamId;

@Immutable
public final class GameResult {
    private final TeamId homeTeam;
    private final int homeScore;
    
    private final TeamId visitingTeam;
    private final int visitingScore;
    
    public GameResult(TeamId homeTeam, int homeScore, TeamId visitingTeam, int visitingScore) {
        this.homeTeam = requireNonNull(homeTeam);
        this.homeScore = checkNotNegative(homeScore);
        this.visitingTeam = requireNonNull(visitingTeam);
        this.visitingScore = checkNotNegative(visitingScore);
    }

    public TeamId getHomeTeamId() {
        return homeTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public TeamId getVisitingTeamId() {
        return visitingTeam;
    }

    public int getVisitingScore() {
        return visitingScore;
    }
    
    public boolean isTie() {
        return homeScore == visitingScore;
    }

    @Override
    public String toString() {
        return String.format("%s %d - %d %s", homeTeam, homeScore, visitingScore, visitingTeam);
    }
}
